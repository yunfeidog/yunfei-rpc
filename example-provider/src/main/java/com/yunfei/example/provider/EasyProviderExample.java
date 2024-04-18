package com.yunfei.example.provider;

import com.yunfei.example.service.UserService;
import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        // RPC初始化
        RpcApplication.init();

        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceAddress(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 启动服务

        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
