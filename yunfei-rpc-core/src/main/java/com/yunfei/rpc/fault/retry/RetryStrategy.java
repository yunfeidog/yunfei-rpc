package com.yunfei.rpc.fault.retry;

import com.yunfei.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略接口
 */
public interface RetryStrategy {
    /**
     * 重试
     * @param callable 重试的方法 代表一个任务
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
