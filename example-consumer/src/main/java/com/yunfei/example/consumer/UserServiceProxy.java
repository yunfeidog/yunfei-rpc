package com.yunfei.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.serializer.JDKSerializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化方式
        JDKSerializer serializer = new JDKSerializer();

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
