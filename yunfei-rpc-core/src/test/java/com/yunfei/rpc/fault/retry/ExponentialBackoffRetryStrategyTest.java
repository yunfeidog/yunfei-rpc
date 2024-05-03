package com.yunfei.rpc.fault.retry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExponentialBackoffRetryStrategyTest {

    @Test
    void doRetry() {
        ExponentialBackoffRetryStrategy exponentialBackoffRetryStrategy = new ExponentialBackoffRetryStrategy();
        assertThrows(Exception.class, () -> exponentialBackoffRetryStrategy.doRetry(() -> {
            throw new Exception();
        }));
    }
}
