package com.yunfei.rpc;

import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.constant.RpcConstant;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC应用
 * 相当于holder ,存放了项目全局用到的变量，双检锁实现单例
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc application init success,config:{}", rpcConfig);
        // // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init success,config:{}", registryConfig);

        // 创建并 注册Shutdown Hook ,JVM 退出时执行擦欧总
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 读取配置文件失败，使用默认配置
            log.error("load config error,use default config", e);
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
