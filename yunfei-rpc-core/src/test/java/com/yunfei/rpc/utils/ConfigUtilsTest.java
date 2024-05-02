package com.yunfei.rpc.utils;

import com.yunfei.rpc.config.RpcConfig;
import org.junit.jupiter.api.Test;

class ConfigUtilsTest {

    @Test
    void loadConfig() {
    }

    @Test
    void testLoadConfig() {


    }

    public static void main(String[] args) {
        // 使用snack包的yaml 读取application.yml resources下的配置文件
    }

    @Test
    void testLoadConfig1() {
    }

    @Test
    void testLoadConfig2() {
    }

    @Test
    void loadProperties() {
    }

    @Test
    void loadYaml() {
        RpcConfig rpcConfig = ConfigUtils.loadYaml(RpcConfig.class, "rpc", "");
        System.out.println(rpcConfig);
    }

    @Test
    void doLoadProperties() {
    }

    @Test
    void doLoadYaml() {
    }
}
