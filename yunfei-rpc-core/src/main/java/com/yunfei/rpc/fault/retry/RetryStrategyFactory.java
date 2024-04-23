package com.yunfei.rpc.fault.retry;

import com.yunfei.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyunfei
 * 重试策略工厂
 */
@Slf4j
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取重试策略实例
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
