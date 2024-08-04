package com.yunfei.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.vertx.core.impl.ConcurrentHashSet;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本地注册节点 key 集合 用于维护续期
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartbeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建Lease 和KV客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建租约 30s
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约绑定 并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中获取
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceCache.readCache();
        if (CollUtil.isNotEmpty(serviceMetaInfoList)) {
            return serviceMetaInfoList;
        }
        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            // cxk ->cxkbaba
            Lease leaseClient = client.getLeaseClient();

            // 创建租约 30s
            long leaseId = leaseClient.grant(30).get().getID();
            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
            kvClient.put(ByteSequence.from("cxk", StandardCharsets.UTF_8), ByteSequence.from("cxkbaba", StandardCharsets.UTF_8), putOption).get();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
            // 解析服务
            List<ServiceMetaInfo> serviceMetaInfos = keyValues.stream().map(keyValue -> {
                String key = keyValue.getValue().toString(StandardCharsets.UTF_8);
                // 监听key的变化
                watch(key);
                return JSONUtil.toBean(key, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            // 写入缓存
            registryServiceCache.writeCache(serviceMetaInfos);
            return serviceMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException("服务发现失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        // 下线时，删除所有注册的服务
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
                System.out.println("下线:" + key);
            } catch (Exception e) {
                throw new RuntimeException(key + "下线失败", e);
            }
        }

        // 关闭客户端
        if (client != null) {
            client.close();
        }

        if (kvClient != null) {
            kvClient.close();
        }

    }

    @Override
    public void heartbeat() {
        // 100s续约一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        // 节点已经过期了，需要重启节点才能重新注册
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点没有过期，重新 注册 相当于续约
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        System.out.println("续约:" + serviceMetaInfo.getServiceNodeKey());
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续约失败", e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 之间未被监听，添加监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), (response) -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }

    }
}
