package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyunfei
 * 快速失败
 */
@Slf4j
public class FailFastTolerantStrategy implements TolerantStrategy {
    /**
     * 快速失败 -立刻通知调用方失败
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("FailFastTolerantStrategy doTolerant", e);
        throw new RuntimeException("FailFastTolerantStrategy doTolerant", e);
    }
}
