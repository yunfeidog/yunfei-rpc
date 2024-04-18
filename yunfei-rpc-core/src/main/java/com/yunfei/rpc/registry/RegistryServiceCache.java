package com.yunfei.rpc.registry;

import com.yunfei.rpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        serviceCache = newServiceCache;
    }

    /**
     * 读缓存
     */
    List<ServiceMetaInfo> readCache() {
        return serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        serviceCache = null;
    }
}
