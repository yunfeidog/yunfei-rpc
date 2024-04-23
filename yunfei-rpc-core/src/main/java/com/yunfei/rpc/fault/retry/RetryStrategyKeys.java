package com.yunfei.rpc.fault.retry;


/**
 * @author houyunfei
 * 重试策略键名常量
 */
public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定间隔重试
     */
    String FIXED_INTERVAL = "fixedInterval";

}
