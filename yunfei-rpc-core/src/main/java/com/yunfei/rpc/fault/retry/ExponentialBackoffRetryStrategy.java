package com.yunfei.rpc.fault.retry;

import com.google.common.base.Stopwatch;
import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ExponentialBackoffRetryStrategy implements RetryStrategy {
    private static final int MAX_RETRY_TIMES = 5;
    private static final long INITIAL_BACKOFF_INTERVAL = 100; // 初始退避时间100毫秒

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        int retryTimes = 0; // 重试次数
        long backoffInterval = INITIAL_BACKOFF_INTERVAL; // 退避时间间隔
        Stopwatch stopwatch = Stopwatch.createUnstarted();  // 计时器

        while (retryTimes < MAX_RETRY_TIMES) {  // 重试次数小于最大重试次数
            try {
                stopwatch.start();
                return callable.call();
            } catch (Exception e) {
                retryTimes++;
                log.warn("RPC call failed, retrying... Current retry times: {}", retryTimes, e);

                stopwatch.stop();
                long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                long sleepTime = Math.min(backoffInterval, Math.max(0, backoffInterval - elapsedTime));
                stopwatch.reset();

                if (sleepTime > 0) {
                    log.info("Backing off for {} ms before next retry.", sleepTime);
                    Thread.sleep(sleepTime);
                }

                backoffInterval *= 2; // 指数退避
            }
        }

        throw new Exception("Maximum retry times exceeded, giving up.");
    }
}
