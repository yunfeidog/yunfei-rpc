package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyunfei
 * 静默处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    /**
     * 静默处理 - 不通知调用方失败，只记录日志
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("FailSafeTolerantStrategy doTolerant", e);
        return new RpcResponse();
    }
}
