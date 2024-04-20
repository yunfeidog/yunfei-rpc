package com.yunfei.rpc.protocol;

import cn.hutool.http.body.RequestBody;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.serializer.Serializer;
import com.yunfei.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws Exception {
        // 分别从指定位置读出Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);

        // 校验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new Exception("Invalid magic!");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // 解决粘包问题，只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnum(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEAT_BEAT:
            case OTHER:
            default:
                throw new RuntimeException("不支持的消息类型");
        }
    }
}
