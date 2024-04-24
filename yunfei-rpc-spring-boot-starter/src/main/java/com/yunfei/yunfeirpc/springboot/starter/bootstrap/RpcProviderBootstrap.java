package com.yunfei.yunfeirpc.springboot.starter.bootstrap;

import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RegistryConfig;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.model.ServiceRegisterInfo;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.server.tcp.VertxTcpServer;
import com.yunfei.yunfeirpc.springboot.starter.annotation.YunRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

/**
 * @author houyunfei
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        YunRpcService rpcService = beanClass.getAnnotation(YunRpcService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
