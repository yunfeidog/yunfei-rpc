package com.yunfei.example.consumer;

import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.rpc.config.RpcConfig;
import com.yunfei.rpc.proxy.ServiceProxyFactory;
import com.yunfei.rpc.utils.ConfigUtils;

public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("yunfei");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("Get user success, name: " + newUser.getName());
        } else {
            System.out.println("Get user failed");
        }


    }


}
