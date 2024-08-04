package com.yunfei.rpc.config;

import com.yunfei.rpc.registry.RegistryKeys;
import lombok.Data;

/**
 * RPC注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间 ms
     */
    private Long timeout = 10000L;
}
