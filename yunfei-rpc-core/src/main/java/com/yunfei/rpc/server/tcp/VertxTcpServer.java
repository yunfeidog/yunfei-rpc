package com.yunfei.rpc.server.tcp;

import com.yunfei.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

/**
 * TCP服务器实现
 */
public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写请求的逻辑，根据requestData构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来编写
        return "hello,client".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建一个Vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建一个TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理连接请求
        server.connectHandler(new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket socket) {
                // 处理连接
                socket.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        byte[] requestData = buffer.getBytes();
                        // 在这里进行自定义的字节数组处理逻辑，比如解析请求，调用服务，构造响应等
                        byte[] responseData = handleRequest(requestData);
                        socket.write(Buffer.buffer(responseData));
                    }
                });
            }
        });

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
