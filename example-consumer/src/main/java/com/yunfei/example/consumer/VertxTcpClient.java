package com.yunfei.example.consumer;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {
    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8082, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to Tcp Server!");
                NetSocket socket = res.result();
                for (int i = 0; i < 1000; i++) {
                    Buffer buffer = Buffer.buffer();
                    String str="hello,server!hello,server!hello,server!hello,server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    System.out.println("Send data to server:" + str);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                // 接收数据
                socket.handler(buffer -> {
                    System.out.println("Received data from server:" + buffer.toString());
                });

            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage()); }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
