package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyunfei
 * 降级到其他服务
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    /**
     * 降级到其他服务 - 降级到其他服务
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 降级到其他服务
        return null;
    }
}
