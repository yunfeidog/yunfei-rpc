package com.yunfei.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.constant.RpcConstant;
import com.yunfei.rpc.fault.retry.RetryStrategy;
import com.yunfei.rpc.fault.retry.RetryStrategyFactory;
import com.yunfei.rpc.loadbalancer.LoadBalancer;
import com.yunfei.rpc.loadbalancer.LoadBalancerFactory;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.protocol.*;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.serializer.JdkSerializer;
import com.yunfei.rpc.serializer.Serializer;
import com.yunfei.rpc.serializer.SerializerFactory;
import com.yunfei.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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

        try {
            // 序列化请求
            byte[] bodyBytes = serializer.serialize(rpcRequest);

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

            // // 发送请求
            // try (HttpResponse httpResponse = HttpRequest.post(metaInfo.getServiceAddress()).body(bodyBytes).execute()) {
            //     byte[] result = httpResponse.bodyBytes();
            //     // 反序列化响应
            //     RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            //     return rpcResponse.getData();
            // }

            // 发送TCP请求
            // 使用重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse response = retryStrategy.doRetry(() -> {
                return VertxTcpClient.doRequest(rpcRequest, metaInfo);
            });
            return response.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
