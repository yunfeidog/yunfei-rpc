package com.yunfei.rpc.server.tcp;

import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.protocol.ProtocolMessage;
import com.yunfei.rpc.protocol.ProtocolMessageDecoder;
import com.yunfei.rpc.protocol.ProtocolMessageEncoder;
import com.yunfei.rpc.protocol.ProtocolMessageTypeEnum;
import com.yunfei.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer->{
            //处理请求
        });
        netSocket.handler(bufferHandlerWrapper);

        // netSocket.handler(buffer -> {
        //     ProtocolMessage<RpcRequest> protocolMessage;
        //     try {
        //         protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
        //     } catch (Exception e) {
        //         throw new RuntimeException("协议消息码错误");
        //     }
        //     RpcRequest rpcRequest = protocolMessage.getBody();
        //
        //     // 处理请求
        //     // 构造响应对象
        //     RpcResponse rpcResponse = new RpcResponse();
        //     Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
        //     try {
        //         // 获取要调用的服务实现类，通过反射调用
        //         Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        //         Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
        //         rpcResponse.setData(result);
        //         rpcResponse.setDataType(method.getReturnType());
        //         rpcResponse.setMessage("success");
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         rpcResponse.setMessage(e.getMessage());
        //         rpcResponse.setException(e);
        //     }
        //
        //     // 发送响应，编码
        //     ProtocolMessage.Header header = protocolMessage.getHeader();
        //     header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
        //     ProtocolMessage<RpcResponse> message = new ProtocolMessage<>(header, rpcResponse);
        //     try {
        //         Buffer encode = ProtocolMessageEncoder.encode(message);
        //         netSocket.write(encode);
        //     } catch (Exception e) {
        //         throw new RuntimeException("协议消息编码错误");
        //     }
        // });

    }

}
