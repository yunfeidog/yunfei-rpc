package com.yunfei.yunfeirpc.springboot.starter.bootstrap;

import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.server.tcp.VertxTcpServer;
import com.yunfei.yunfeirpc.springboot.starter.annotation.EnableYunRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author houyunfei
 */
@Slf4j
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring初始化执行时候，初始化Rpc框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableYunRpc.class.getName()).get("needServer");

        // Rpc框架初始化（配置和注册中心）
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("Rpc server is not started");
        }
    }
}
