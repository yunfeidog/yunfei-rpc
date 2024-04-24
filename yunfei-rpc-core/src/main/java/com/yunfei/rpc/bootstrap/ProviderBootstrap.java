package com.yunfei.rpc.bootstrap;

import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.model.ServiceRegisterInfo;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author houyunfei
 */
@Slf4j
public class ProviderBootstrap {
    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC初始化
        RpcApplication.init();


        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            Class<?> implClass = serviceRegisterInfo.getImplClass();

            // 本地注册
            LocalRegistry.register(serviceName, implClass);

            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 启动服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
