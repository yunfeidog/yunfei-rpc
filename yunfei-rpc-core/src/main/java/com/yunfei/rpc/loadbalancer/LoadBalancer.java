package com.yunfei.rpc.loadbalancer;

import com.yunfei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器 （消费端使用）
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
