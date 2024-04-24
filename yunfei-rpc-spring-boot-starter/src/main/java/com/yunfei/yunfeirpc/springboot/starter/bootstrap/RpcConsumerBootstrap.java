package com.yunfei.yunfeirpc.springboot.starter.bootstrap;

import com.yunfei.rpc.proxy.ServiceProxyFactory;
import com.yunfei.yunfeirpc.springboot.starter.annotation.YunRpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author houyunfei
 * 服务消费者启动类
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            YunRpcReference rpcReference = field.getAnnotation(YunRpcReference.class);
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                System.out.println("生成代理对象:" + interfaceClass.getName()+"  "+field.getType());
                field.setAccessible(true);
                log.info("生成代理对象:{}", interfaceClass.getName());
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    System.out.println("生成代理对象失败");
                    throw new RuntimeException(e);
                }
            }

        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
