package com.yunfei.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo metaInfo) throws Exception {
        // 发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(metaInfo.getServicePort(), metaInfo.getServiceHost(), res -> {
            if (!res.succeeded()) {
                System.err.println("Failed to connect to TCP server");
                return;
            }
            System.out.println("Connected to TCP server");
            NetSocket socket = res.result();

            // 发送数据
            ProtocolMessage<Object> protocolMessage = new ProtocolMessage<>();
            ProtocolMessage.Header header = new ProtocolMessage.Header();

            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
            header.setRequestId(IdUtil.getSnowflakeNextId());

            protocolMessage.setHeader(header);
            protocolMessage.setBody(rpcRequest);
            // 编码请求
            try {
                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                socket.write(encodeBuffer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 接收响应
            TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                try {
                    ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                    responseFuture.complete(responseProtocolMessage.getBody());
                } catch (Exception e) {
                    throw new RuntimeException("协议消息码错误");
                }

            });
            socket.handler(tcpBufferHandlerWrapper);


        });
        System.out.println("Waiting for response");
        RpcResponse rpcResponse = null;
        rpcResponse = responseFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response");
        netClient.close();
        return rpcResponse;
    }

    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8082, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to Tcp Server!");
                NetSocket socket = res.result();
                for (int i = 0; i < 1000; i++) {
                    Buffer buffer = Buffer.buffer();
                    String str = "hello,server!hello,server!hello,server!hello,server!";
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
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
