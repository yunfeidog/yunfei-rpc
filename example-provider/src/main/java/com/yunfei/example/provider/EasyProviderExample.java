package com.yunfei.example.provider;

import com.yunfei.example.service.UserService;
import com.yunfei.rpc.registry.LocalRegistry;
import com.yunfei.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }
}
