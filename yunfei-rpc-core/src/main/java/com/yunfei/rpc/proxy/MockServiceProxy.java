package com.yunfei.rpc.proxy;

import com.github.jsonzou.jmockdata.JMockData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理 JDK动态代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型，生成特定的默认值对象
        Class<?> returnType = method.getReturnType();
        Object mockData = JMockData.mock(returnType);
        log.info("mockData:{}", mockData);
        return mockData;
    }
}
