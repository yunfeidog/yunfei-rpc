package com.yunfei.rpc;

import com.yunfei.rpc.config.RpcConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RpcApplicationTest {

    @Test
    void init() {
    }

    @Test
    void testInit() {
    }

    @Test
    void getRpcConfig() {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println(rpcConfig);
    }
}
