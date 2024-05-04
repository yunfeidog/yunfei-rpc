---
date: 2024-05-02
title: 接口Mock服务
order: 4
---

# 接口Mock服务

## 什么是接口Mock服务？

接口 Mock 服务是一种用于模拟接口行为的技术,它的主要目的是为了在实际服务还未就绪的情况下,提供模拟的接口数据,使得依赖该接口的其他团队或系统可以独立进行开发和测试工作。

## 为什么要接口Mock服务？

​		在传统前后端分离开发的场景下，前端和后端一般定好 HTTP API 接口后就各自进行开发，前端开发中使用 EasyMock、webpack-api-mock 等平台/工具进行接口的 mock，后端通过 Postman / curl 等工具进行接口的自测。

​		在微服务场景下，各服务之间通过 IDL 定义好 RPC 接口。但是接口调用方依然有 mock 接口的需求，接口提供方也有着自测接口的需求。公司内的服务化平台已经提供了较为完善的接口测试工具，自己实现一个相对也比较容易，但目前却没有一个比较完善的 RPC Mock 方案。

​		在新项目启动后，前端、API 层和依赖的 Service 往往同步开始开发，只要依赖的 Service 未提供，API 和前端的开发、自测都会被阻塞，在侧重数据展示类需求的项目中这种问题更加严重。

​		所以，有必要尝试探索一套 RPC Mock 的方案，在保证开发者使用体验的前提下，解决上述问题。除此之外 ，使用接口 Mock 服务还主要有以下几个原因:

1. 提高开发效率:减少等待实际服务开发完成的时间,从而加快开发进度。

2. 降低成本和风险:在开发早期发现问题,避免后期集成时出现的问题。

3. 支持自动化测试:提供可控的测试环境,有助于实现更全面的自动化测试。

4. 模拟异常场景:模拟各种异常情况,如超时、错误码等,帮助开发人员进行容错性测试。

5. 提高系统可用性:当实际服务不可用时,作为备用方案提高整体系统的可用性。

## Mock服务实现

我们可以使用动态代理，在创建调用方法的时候返回固定值对象即可。

依赖安装：

```xml
<dependency>
    <groupId>com.github.jsonzou</groupId>
    <artifactId>jmockdata</artifactId>
    <version>4.3.0</version>
</dependency>
```

获取Mock代理的 工厂：

```java
public class ServiceProxyFactory {
    /**
     * 根据服务类 获取Mock代理对象
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }
}
```

Mock代理的具体实现：

如下，我们是通过JMockData来实现模拟数据的获取

```java
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
```

测试：

```java
@Slf4j
public class MockTest {
    public static void main(String[] args) {
        // 获取 代理
        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
        short number = userService.getNumber();
        log.info("number:{}", number);
        User user = userService.getUser(new User());
        log.info("user:{}", user);
    }

}
```

运行结果：

![image-20240502162101815](https://s2.loli.net/2024/05/02/J7N5GXgQxtnTzjL.webp)
