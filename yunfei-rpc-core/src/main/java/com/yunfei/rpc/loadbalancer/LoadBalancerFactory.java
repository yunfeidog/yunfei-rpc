package com.yunfei.rpc.loadbalancer;

import com.yunfei.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RandomLoadBalancer();

    /**
     * 获取负载均衡器
     *
     * @param key 负载均衡器键名
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
