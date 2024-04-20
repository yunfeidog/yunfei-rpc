package com.yunfei.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEAT_BEAT(2),
    OTHER(3);


    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * 根据key获取枚举
     */
    public static ProtocolMessageTypeEnum getEnum(int key) {
        for (ProtocolMessageTypeEnum typeEnum : values()) {
            if (typeEnum.getKey() == key) {
                return typeEnum;
            }
        }
        return null;
    }
}
