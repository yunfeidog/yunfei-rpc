package com.yunfei.rpc.server.tcp;

import com.yunfei.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * TCP服务器实现
 */
public class VertxTcpServer implements HttpServer {


    @Override
    public void doStart(int port) {
        // 创建一个Vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建一个TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理连接请求
        server.connectHandler(new TcpServerHandler());

        // 启动TCP服务器并监听指定端口
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("TCP server is now listening on actual port: " + server.actualPort());
            } else {
                System.err.println("Failed to bind!");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8080);
    }
}
