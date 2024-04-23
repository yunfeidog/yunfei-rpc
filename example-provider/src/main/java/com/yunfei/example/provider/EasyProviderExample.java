package com.yunfei.example.provider;

import com.yunfei.example.service.UserService;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        System.out.println("UserService.class.getname="+UserService.class.getName());
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动服务
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
