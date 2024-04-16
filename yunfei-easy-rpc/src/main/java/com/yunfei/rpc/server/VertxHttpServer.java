package com.yunfei.rpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            // 处理HTTP请求
            System.out.println("Received request: " + request.method() + " " + request.uri());
            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        });

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动HTTP服务 监听端口
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to bind!");
            }
        });
    }
}
