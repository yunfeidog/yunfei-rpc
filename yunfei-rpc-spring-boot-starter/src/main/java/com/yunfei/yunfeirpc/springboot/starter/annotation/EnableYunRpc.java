package com.yunfei.yunfeirpc.springboot.starter.annotation;

import com.yunfei.yunfeirpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.yunfei.yunfeirpc.springboot.starter.bootstrap.RpcInitBootStrap;
import com.yunfei.yunfeirpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootStrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableYunRpc {

    /**
     * 需要启动server
     *
     * @return
     */
    boolean needServer() default true;
}
