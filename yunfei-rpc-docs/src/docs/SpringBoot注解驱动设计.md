---
date: 2024-05-04
title: SpringBoot注解驱动设计
---

# SpringBoot注解驱动设计

在Dubbo中，我们想要使用Dubbo框架的RPC远程调用功能，主要有三步：

+ 在启动类上加`@EnableDubbo`
+ 在提供方类上加：`@DubboService`
+ 在消费者注入的类上加：`@DubboReference`

我们参考Dubbo，也开发这样三个注解就可以使用整个RPC框架

## 项目初始化

新建一个SpringBoot项目，加入相关依赖，插件，以及build都不需要

```xml
<properties>
    <java.version>17</java.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo</artifactId>
        <version>3.0.9</version>
    </dependency>
    <dependency>
        <groupId>com.yunfei</groupId>
        <artifactId>yunfei-rpc-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

这个插件的主要作用是用户在写yml的时候可以有注释

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

## 注解设计

### @EnableYunRpc

```java
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
```

 `@EnableYunRpc` 注解的设计:

1. `@Target({ElementType.TYPE})`:
   - 这个注解用于指定 `@EnableYunRpc` 注解可以被应用于哪些程序元素。在这里,它被限定为只能应用于类型(Type)级别,也就是类、接口或枚举。

2. `@Retention(RetentionPolicy.RUNTIME)`:
   - 这个注解用于指定 `@EnableYunRpc` 注解的保留策略。`RUNTIME` 表示该注解会在运行时被 JVM 读取和使用,可以被反射机制访问。

3. `@Import({RpcInitBootStrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})`:
   - 这个注解用于导入其他配置类。在这里,它导入了三个引导类:
     - `RpcInitBootStrap`: RPC 应用程序的初始化引导类。
     - `RpcProviderBootstrap`: RPC 服务提供者的引导类。
     - `RpcConsumerBootstrap`: RPC 服务消费者的引导类。
   - 当 `@EnableYunRpc` 注解被应用到一个类上时,Spring 容器会自动注册这三个引导类。

4. `public @interface EnableYunRpc {}`:
   - 这是一个自定义注解的声明,名为 `@EnableYunRpc`。通过这个注解,开发者可以在自己的应用程序中开启 RPC 相关的功能。

5. `boolean needServer() default true;`:
   - 这是 `@EnableYunRpc` 注解中定义的一个属性。它用于指定是否需要启动 RPC 服务端。默认值为 `true`。
   - 开发者可以通过设置这个属性的值来决定是否需要启动 RPC 服务端,例如在仅作为 RPC 客户端的场景下,可以将其设置为 `false`。

这个 `@EnableYunRpc` 注解是一个基于 Spring 的注解驱动设计模式,它可以帮助开发者快速地在自己的应用程序中集成 RPC 功能。通过将引导类的注册和初始化过程封装在这个注解中,开发者只需要简单地在入口类上添加该注解,就可以自动完成 RPC 相关的配置和初始化。

### @YunRpcService

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface YunRpcService {

    /**
     * 服务接口类
     *
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     *
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
```

 `@YunRpcService` 注解主要用于标注 RPC 服务提供者。

1. `@Target({ElementType.TYPE})`:
   - 这个元注解指定了 `@YunRpcService` 注解可以被应用于类型(Type)级别,也就是类、接口或枚举。

2. `@Retention(RetentionPolicy.RUNTIME)`:
   - 这个元注解指定了 `@YunRpcService` 注解的保留策略是在运行时被 JVM 读取和使用。

3. `@Component`:
   - 这个元注解将 `@YunRpcService` 注解标记为 Spring 组件,意味着被这个注解标注的类会被 Spring 容器自动扫描和注册。

4. `public @interface YunRpcService {}`:
   - 这是 `@YunRpcService` 注解本身的声明。它定义了这个注解的名称和作用域。

5. `Class<?> interfaceClass() default void.class;`:
   - 这是 `@YunRpcService` 注解定义的一个属性,用于指定服务接口类。
   - 如果不设置该属性,默认值为 `void.class`。

6. `String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;`:
   - 这是 `@YunRpcService` 注解定义的另一个属性,用于指定服务的版本号。
   - 如果不设置该属性,默认值为 `RpcConstant.DEFAULT_SERVICE_VERSION`。

`@YunRpcService` 注解的设计目的是为了简化 RPC 服务提供者的配置。当一个类被这个注解标注时,Spring 容器会自动扫描并注册该服务,同时也会提取服务接口类和版本号等元信息。这些信息可以在后续的服务发现和调用过程中使用。

### @YunRpcReference

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YunRpcReference {

    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡策略
     * @return
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     * @return
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错策略
     * @return
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 是否mock
     * @return
     */
    boolean mock() default false;
}
```

这个 `@YunRpcReference` 注解是用于标注 RPC 服务消费者端的注解。

1. `@Target({ElementType.FIELD})`:
   - 这个元注解指定了 `@YunRpcReference` 注解只能被应用在字段(Field)级别。

2. `@Retention(RetentionPolicy.RUNTIME)`:
   - 这个元注解指定了 `@YunRpcReference` 注解的保留策略是在运行时被 JVM 读取和使用。

3. `public @interface YunRpcReference {}`:
   - 这是 `@YunRpcReference` 注解本身的声明。它定义了这个注解的名称和作用域。

4. `Class<?> interfaceClass() default void.class;`:
   - 这是 `@YunRpcReference` 注解定义的一个属性,用于指定服务接口类。
   - 如果不设置该属性,默认值为 `void.class`。

5. `String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;`:
   - 这是 `@YunRpcReference` 注解定义的另一个属性,用于指定服务的版本号。
   - 如果不设置该属性,默认值为 `RpcConstant.DEFAULT_SERVICE_VERSION`。

6. `String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;`:
   - 这是 `@YunRpcReference` 注解定义的一个属性,用于指定负载均衡策略。
   - 如果不设置该属性,默认值为 `LoadBalancerKeys.ROUND_ROBIN`。

7. `String retryStrategy() default RetryStrategyKeys.NO;`:
   - 这是 `@YunRpcReference` 注解定义的一个属性,用于指定重试策略。
   - 如果不设置该属性,默认值为 `RetryStrategyKeys.NO`。

8. `String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;`:
   - 这是 `@YunRpcReference` 注解定义的一个属性,用于指定容错策略。
   - 如果不设置该属性,默认值为 `TolerantStrategyKeys.FAIL_FAST`。

9. `boolean mock() default false;`:
   - 这是 `@YunRpcReference` 注解定义的一个属性,用于指定是否使用 Mock 模式。
   - 如果不设置该属性,默认值为 `false`。

总的来说,`@YunRpcReference` 注解的设计目的是为了简化 RPC 服务消费者的配置。当一个字段被这个注解标注时,Spring 容器会自动注入一个代理对象,该代理对象会负责执行 RPC 调用。开发者可以通过设置注解属性来配置负载均衡、重试、容错等策略,以满足不同的业务需求。

## 注解驱动

### 全局启动类

我们希望在Spring框架初始化的时候，能够获取`@EnableYunRpc`注解，并且初始化RPC框架。

可以使用Spring的`ImportBeanDefinitionRegistrar`接口来实现，此接口用于在 Spring 容器初始化时执行自定义的注册逻辑。

具体的自定义的注册逻辑写在`registerBeanDefinitions`方法里面

```java
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
```

这个 `RpcInitBootStrap` 类负责在 Spring 容器初始化时执行 RPC 框架的初始化和服务端启动逻辑。

它利用 `@EnableYunRpc` 注解中的 `needServer` 属性,来决定是否需要启动 RPC 服务端。这种设计可以让 RPC 框架在 Spring Boot 应用中更加灵活和可配置。

### 提供者启动

提供者需要获取到所有包含`@YunRpcService`的注解的类，然后利用反射机制，获取到对应的注册信息，完成服务信息的注册。

我们让启动类实现`BeanPostProcessor`接口里的`postProcessAfterInitialization`方法，就可以在服务提供者Bean初始化之后，执行注册服务等操作了。

```java
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
```

1. 获取服务基本信息:
   - 首先从 `@YunRpcService` 注解中获取 `interfaceClass` 属性的值。这个属性指定了服务的接口类。
   - 如果 `interfaceClass` 的值为 `void.class`(默认值),则说明开发者没有手动指定接口类,于是取当前 Bean 实例的第一个接口作为服务接口。
   - 使用服务接口类的名称作为 `serviceName`。
   - 从 `@YunRpcService` 注解中获取 `serviceVersion` 属性的值。

2. 本地注册服务:
   - 使用 `LocalRegistry.register(serviceName, beanClass)` 方法将服务信息注册到本地注册表中。这样在后续的 RPC 调用中,就可以从本地注册表中获取到服务的实现类。

### 消费者启动

 `RpcConsumerBootstrap` 类是 RPC 服务消费者的引导类,它同样实现了 Spring 的 `BeanPostProcessor` 接口。它的主要作用是在 Spring 容器初始化 Bean 实例后,检查这些 Bean 中是否有被 `@YunRpcReference` 注解标注的字段,如果有,则为这些字段生成代理对象并注入。

```java
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
```



让我们逐步解析这个类的实现:

1. `Field[] declaredFields = beanClass.getDeclaredFields();`:这行代码获取当前 Bean 实例的所有声明字段。
2. `YunRpcReference rpcReference = field.getAnnotation(YunRpcReference.class);`:如果字段被 `@YunRpcReference` 注解标注,则获取该注解实例。
3. `if (rpcReference != null) { ... }`:
   - 如果字段被 `@YunRpcReference` 注解标注,则执行以下逻辑:
     - 获取服务接口类,如果未指定则默认使用字段类型。
     - 使用 `ServiceProxyFactory.getProxy(interfaceClass)` 方法为服务接口生成代理对象。
     - 将生成的代理对象设置到当前 Bean 实例的字段上。
4. `return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);`:
   - 最后,调用父类 `BeanPostProcessor` 的 `postProcessAfterInitialization()` 方法,确保其他 `BeanPostProcessor` 实现也能正确执行。

### 注册已编写的启动类

最后还需要在启动类上面使用`@Import`到注册我们自定义的启动类

```java
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
```

## 测试

见快速入门：[快速入门](./快速入门.md)
