package com.yunfei.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.constant.RpcConstant;
import com.yunfei.rpc.constant.TolerantStrategyConstant;
import com.yunfei.rpc.fault.retry.RetryStrategy;
import com.yunfei.rpc.fault.retry.RetryStrategyFactory;
import com.yunfei.rpc.fault.tolerant.TolerantStrategy;
import com.yunfei.rpc.fault.tolerant.TolerantStrategyFactory;
import com.yunfei.rpc.loadbalancer.LoadBalancer;
import com.yunfei.rpc.loadbalancer.LoadBalancerFactory;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.registry.Registry;
import com.yunfei.rpc.registry.RegistryFactory;
import com.yunfei.rpc.serializer.Serializer;
import com.yunfei.rpc.serializer.SerializerFactory;
import com.yunfei.rpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
