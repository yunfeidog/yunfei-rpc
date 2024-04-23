package com.yunfei.rpc.loadbalancer;

import com.yunfei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器 （消费端使用）
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性Hash环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 构建虚拟节点环 每次都重新构建，因为服务列表可能会变化 为了能够及时感知到服务的变化
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        int hash = getHash(requestParams);

        // 选择最接近且大于hash的节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * Hash算法
     *
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
