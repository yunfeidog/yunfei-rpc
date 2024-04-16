package com.yunfei.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse  implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型(预留)
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;


    /**
     * 异常信息
     */
    private Exception exception;
}
