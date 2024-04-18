package com.yunfei.rpc.registry;

import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务 服务端
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 取消注册服务 服务端
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现  获取某服务的所有节点  客户端 消费端
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测
     */
    void heartbeat();

    /**
     * 监听服务节点
     */
    void watch(String serviceNodeKey);
}
