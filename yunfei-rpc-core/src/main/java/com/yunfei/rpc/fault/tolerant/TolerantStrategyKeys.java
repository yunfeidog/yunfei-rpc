package com.yunfei.rpc.fault.tolerant;


/**
 * @author houyunfei
 */
public interface TolerantStrategyKeys {

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

}
