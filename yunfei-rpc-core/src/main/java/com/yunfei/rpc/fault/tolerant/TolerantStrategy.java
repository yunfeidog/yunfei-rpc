package com.yunfei.rpc.fault.tolerant;


import com.yunfei.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author houyunfei
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
