package com.yunfei.rpc.server.tcp;

import cn.hutool.json.JSONUtil;
import com.yunfei.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

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
                        RecordParser parser = RecordParser.newFixed(8);

                        parser.setOutput(new Handler<Buffer>() {
                            // 初始化
                            int size = -1;
                            // 一次性读取完整的（头+体）
                            Buffer resultBuffer = Buffer.buffer();

                            @Override
                            public void handle(Buffer buffer) {
                                if (-1 == size) {
                                    // 读取消息体的长度
                                    size = buffer.getInt(4);
                                    parser.fixedSizeMode(size);
                                    // 写入头信息到结果
                                    resultBuffer.appendBuffer(buffer);
                                    System.out.println("读取消息体的长度:" + size + "，头信息：" + resultBuffer.toString());
                                } else {
                                    // 写入体信息到结果
                                    resultBuffer.appendBuffer(buffer);
                                    System.out.println("Received data from client:" + resultBuffer.toString());
                                    parser.fixedSizeMode(8);
                                    size = -1;
                                    resultBuffer = Buffer.buffer();
                                }
                            }
                        });
                        socket.handler(parser);
                        // byte[] requestData = buffer.getBytes();
                        // String testMessage = "hello,server!hello,server!hello,server!hello,server!";
                        // int messageLength = testMessage.getBytes().length;
                        //
                        // // 解决粘包和半包问题
                        // RecordParser parser = RecordParser.newFixed(messageLength);
                        // parser.setOutput(buffer1 -> {
                        //     String str = new String(buffer1.getBytes());
                        //     System.out.println("str=" + str);
                        //     if (testMessage.equals(str)) {
                        //         System.out.println("没有半包和粘包问题good message");
                        //         socket.write("hello,client good message");
                        //     }
                        // });
                        // socket.handler(parser);


                        // if (buffer.getBytes().length < messageLength) {
                        //     System.out.println("半包,length:" + buffer.getBytes().length);
                        //     return;
                        // }
                        // if (buffer.getBytes().length > messageLength) {
                        //     System.out.println("粘包,length:" + buffer.getBytes().length);
                        //     return;
                        // }
                        //
                        //
                        // String str = new String(buffer.getBytes(0, messageLength));
                        // System.out.println("Received data from client:" + str);
                        // if (testMessage.equals(str)) {
                        //     System.out.println("good message");
                        //     socket.write("hello,client good message");
                        // }


                        // 在这里进行自定义的字节数组处理逻辑，比如解析请求，调用服务，构造响应等
                        // byte[] responseData = handleRequest(requestData);
                        // socket.write(Buffer.buffer(responseData));
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
