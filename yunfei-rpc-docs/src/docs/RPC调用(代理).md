---
date: 2024-05-04
title: RPC调用(代理)
order: 3
---

# RPC调用(代理)

## 什么是代理？

在 RPC (远程过程调用) 中,代理(Proxy)是一个非常重要的概念。代理对象是客户端用来调用远程服务的中间层,它可以帮助客户端屏蔽掉远程调用的细节。

代理模式是一种常见的设计模式,它为一个对象提供一个替身,以控制对这个对象的访问。在 RPC 中,代理对象扮演着这样的角色:

1. **隐藏远程调用细节**:
   - 客户端只需要调用代理对象的方法,而不需要关心远程服务的地址、协议、序列化等细节。
   - 代理对象会负责将客户端的请求转换为远程服务能够理解的格式,并将结果转换回客户端期望的格式。

2. **支持中间件功能**:
   - 代理对象可以在转发请求和响应的过程中,增加额外的功能,如负载均衡、重试、熔断、监控等。
   - 这些功能都是在客户端感知不到的情况下完成的,提高了系统的可靠性和可扩展性。

3. **抽象客户端与服务端的耦合**:
   - 客户端只需要依赖代理对象,而不需要直接依赖远程服务的接口定义。
   - 这样可以降低客户端与服务端的耦合度,提高系统的灵活性和可维护性。

在 RPC 框架中,代理对象通常是由客户端动态生成的,使用了动态代理的技术。这样客户端可以无感知地调用远程服务,而代理对象会负责完成各种中间件功能,为客户端提供一个简单、可靠的远程调用接口。

## 静态代理

静态代理就是自己去写一个实现类，但是这种办法缺点很明显，每个类都要去写实现类

+ 静态代理是在编译时就已经生成代理类的字节码文件。
+ 代理类的实现是手动编写的,需要实现与目标类相同的接口,并在内部调用目标类的方法。
+ 静态代理的优点是实现简单,可以在代理类中添加额外的功能。缺点是如果目标类有变化,则需要修改代理类的代码。

在common模块里面，我们定一个接口`UserService`

![image-20240504103240434](https://s2.loli.net/2024/05/04/GUt4WBA3ljsi5ON.webp)

consumer通过静态代理调用，直接new出UserServiceProxy对象进行调用

```java
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserServiceProxy userServiceProxy = new UserServiceProxy();
        userServiceProxy.getNumber();
    }
}
```

在consumer模块里面，我们实现静态代理`UserServiceProxy`

```java
/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化方式
        JdkSerializer serializer = new JdkSerializer();

        // 发送请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            String url = "http://localhost:8080";
            try (HttpResponse httpResponse = HttpRequest.post(url).body(bodyBytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

上面的代码是通过http来请求拿到provider提供者的实现函数，具体在设置`methodName`为调用的函数

```java
// 发送请求
RpcRequest rpcRequest = RpcRequest.builder()
        .serviceName(UserService.class.getName())
        .methodName("getUser")
        .parameterTypes(new Class[]{User.class})
        .args(new Object[]{user})
        .build();
```

下面是provider提供的具体代码，也就是具体的操作

```java
public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("UserServiceImpl.getUser username=" + user.getName());
        return user;
    }
}
```

当然，服务提供者也要提前把对应的实现类进行注册,否则将找不到实现类：

```java
public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动服务
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
```

## 动态代理

+ 动态代理是在运行时通过反射机制动态地创建代理类的实例。
+ 动态代理不需要事先编写代理类的源码,而是在运行时根据需要动态生成代理类的字节码。
+ 动态代理的优点是灵活性高,可以很方便地对目标类进行功能增强,并且不需要修改目标类的代码。缺点是实现相对复杂,需要使用反射等高级编程技术。

在 RPC 框架中,通常会采用动态代理的方式来实现客户端的代理对象。这样可以做到不修改客户端代码的情况下,就可以为目标服务添加各种中间件功能,如负载均衡、失败重试、熔断保护等。

Java 中常见的动态代理实现方式有:

1. JDK 动态代理:使用 `java.lang.reflect.Proxy` 类及其相关接口实现。
2. CGLib 动态代理:使用字节码技术在运行时动态生成代理类。

具体实现：

```java
/**
 * 动态代理
 */
public class ServiceProxy implements InvocationHandler {

    // 指定序列化器
    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();


        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无可用服务提供者");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo metaInfo = loadBalancer.select(requestParams, serviceMetaInfos);

        // 发送TCP请求
        // 使用重试策略
        RpcResponse response ;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            response = retryStrategy.doRetry(() -> {
                return VertxTcpClient.doRequest(rpcRequest, metaInfo);
            });
        } catch (Exception e) {
            TolerantStrategy strategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            // 构造上下文
            Map<String, Object> context = new HashMap<>();
            context.put(TolerantStrategyConstant.SERVICE_LIST, serviceMetaInfos);
            context.put(TolerantStrategyConstant.CURRENT_SERVICE, metaInfo);
            context.put(TolerantStrategyConstant.RPC_REQUEST, rpcRequest);
            response = strategy.doTolerant(context, e);
        }
        return response.getData();
    }
}

```

解释：

我们想要用动态代理，就需要去继承InvocationHandler接口，然后实现invoke方法

1. `ServiceProxy` 类实现了 `InvocationHandler` 接口:
   - `InvocationHandler` 是 Java 动态代理的核心接口,它定义了 `invoke()` 方法,当代理对象的方法被调用时,会自动调用这个方法。

2. `invoke()` 方法的实现:
   - 在这个方法中,首先根据反射获取到的方法信息,构建了一个 `RpcRequest` 对象,包含了服务名、方法名、参数类型和参数值等信息。
   - 这个 `RpcRequest` 对象就是动态代理要封装和处理的核心请求信息。

3. 动态代理的功能实现:
   - 在 `invoke()` 方法中,我们看到了从注册中心查询服务提供者信息、负载均衡选择服务提供者、使用重试策略和容错策略执行 RPC 调用等功能。
   - 这些功能都是在动态代理层面实现的,客户端调用时无需关心这些细节,只需要调用代理对象的方法即可。

4. 返回结果处理:
   - 最后,`invoke()` 方法将服务提供者返回的 `RpcResponse` 对象转换为方法调用的返回结果,并返回给客户端。

这段代码体现了动态代理的核心思想,即为客户端提供一个透明的代理层,在代理层面封装并处理 RPC 调用的各种细节,让客户端可以像调用本地方法一样使用远程服务,提高了系统的可靠性和可扩展性。



## 动态代理工厂

```java

public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    /**
     * 根据服务类 获取Mock代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
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

这个 `ServiceProxyFactory` 类提供了两个静态方法,用于生成 RPC 客户端的代理对象:

1. `getProxy(Class<T> serviceClass)` 方法:
   - 该方法首先检查 RPC 配置是否开启了 Mock 模式。
   - 如果开启了 Mock 模式,则调用 `getMockProxy()` 方法生成一个 Mock 代理对象。
   - 如果未开启 Mock 模式,则使用 Java 动态代理创建一个 `ServiceProxy` 对象作为代理。

2. `getMockProxy(Class<T> serviceClass)` 方法:
   - 该方法使用 Java 动态代理的方式,创建一个 `MockServiceProxy` 对象作为代理。

动态代理的实现主要有两个步骤:

1. 创建动态代理实例:
   - 使用 `Proxy.newProxyInstance()` 方法创建动态代理实例。
   - 该方法需要传入三个参数:
     1. 目标类的类加载器 (`serviceClass.getClassLoader()`)
     2. 目标类实现的接口数组 (`new Class[]{serviceClass}`)
     3. 实现 `InvocationHandler` 接口的代理类实例 (`new ServiceProxy()` 或 `new MockServiceProxy()`)

2. 返回代理对象:
   - `Proxy.newProxyInstance()` 方法返回一个动态生成的代理对象,该对象实现了目标类的所有接口方法。
   - 最后将这个代理对象强制转换为目标类型 `(T)` 并返回。

通过这个 `ServiceProxyFactory` 类,客户端可以非常方便地获取到 RPC 服务的代理对象,无需关心代理对象的具体实现细节。如果需要切换到 Mock 模式,只需要在配置中开启 Mock 功能即可。

这种代理工厂的设计模式可以很好地封装动态代理的创建逻辑,提高代码的可维护性和扩展性。同时,它也体现了面向接口编程的设计思想,客户端只需要依赖目标服务的接口,而不需要依赖具体的代理实现。
