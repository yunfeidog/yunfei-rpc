package com.yunfei.rpc.protocol;

/**
 * 协议常量
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAAGE_HEADER_LENGTH= 17;

    /**
     * 魔数
     */
    byte PROTOCOL_MAGIC = 0x01;

    /**
     * 协议版本
     */
    byte PROTOCOL_VERSION = 0x01;
}
