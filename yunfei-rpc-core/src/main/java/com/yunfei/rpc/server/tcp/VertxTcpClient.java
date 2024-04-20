package com.yunfei.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {
    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to Tcp Server!");
                NetSocket socket = res.result();
                // 发送数据
                socket.write("hello,server");

                // 接收数据
                socket.handler(buffer -> {
                    System.out.println("Received data from server:" + buffer.toString());
                });

            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
