package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyunfei
 */
@Slf4j
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }


    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
