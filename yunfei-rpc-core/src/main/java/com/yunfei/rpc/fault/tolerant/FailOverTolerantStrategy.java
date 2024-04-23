package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyunfei
 * 故障转移
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    /**
     * 故障转移 - 重试其他服务
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //todo 重试其他服务
        return null;
    }
}
