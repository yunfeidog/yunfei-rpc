package com.yunfei.rpc.fault.retry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearRetryStrategyTest {

    @Test
    void doRetry() {
        LinearRetryStrategy linearRetryStrategy = new LinearRetryStrategy();
        assertThrows(Exception.class, () -> linearRetryStrategy.doRetry(() -> {
            throw new Exception("RPC call failed");
        }));
    }
}
