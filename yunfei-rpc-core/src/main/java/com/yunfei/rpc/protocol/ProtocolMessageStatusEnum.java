package com.yunfei.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("成功", 0),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50),
    ;
    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     */
    public static ProtocolMessageStatusEnum getEnum(int value) {
        for (ProtocolMessageStatusEnum statusEnum : values()) {
            if (statusEnum.getValue() == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
