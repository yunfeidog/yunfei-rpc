package com.yunfei.rpc.fault.retry;

import com.google.common.base.Stopwatch;
import com.yunfei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LinearRetryStrategy implements RetryStrategy {
    private static final int MAX_RETRY_TIMES = 5;
    private static final long INITIAL_BACKOFF_INTERVAL = 1000; // 初始退避时间1秒

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        int retryTimes = 0;
        long backoffInterval = INITIAL_BACKOFF_INTERVAL;
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        while (retryTimes < MAX_RETRY_TIMES) {
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

                backoffInterval += INITIAL_BACKOFF_INTERVAL; // 线性增加退避时间
            }
        }

        throw new Exception("Maximum retry times exceeded, giving up.");
    }
}
