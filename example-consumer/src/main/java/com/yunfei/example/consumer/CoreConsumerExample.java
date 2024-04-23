package com.yunfei.example.consumer;

import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.proxy.ServiceProxyFactory;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.server.tcp.VertxTcpClient;
import com.yunfei.rpc.utils.ConfigUtils;

public class CoreConsumerExample {
    public static void main(String[] args) {
        // 静态代理
        // UserService userService = new UserServiceProxy();


        // 动态代理
        // testDynamicProxy();

        // Mock代理
        // testMock();

        // 加载配置
        // testLoadConfig();

        new VertxTcpClient().start();

    }

    private static void testDynamicProxy() {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yunfei");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("Get user success, name: " + newUser.getName());
        } else {
            System.out.println("Get user failed");
        }
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
    }

    static void testMock() {
        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
        short number = userService.getNumber();
        System.out.println("Number: " + number);
    }

    static void testLoadConfig() {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
    }
}
