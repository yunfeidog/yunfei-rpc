package com.yunfei.rpc.fault.tolerant;

import com.yunfei.rpc.RpcApplication;
import com.yunfei.rpc.constant.TolerantStrategyConstant;
import com.yunfei.rpc.fault.retry.RetryStrategy;
import com.yunfei.rpc.fault.retry.RetryStrategyFactory;
import com.yunfei.rpc.model.RpcRequest;
import com.yunfei.rpc.model.RpcResponse;
import com.yunfei.rpc.model.ServiceMetaInfo;
import com.yunfei.rpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author houyunfei
 * 降级到其他服务
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    /**
     * 降级到其他服务 - 重试其他服务
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        List<ServiceMetaInfo> metaInfos = (List<ServiceMetaInfo>) context.get(TolerantStrategyConstant.SERVICE_LIST);
        ServiceMetaInfo metaInfo = (ServiceMetaInfo) context.get(TolerantStrategyConstant.CURRENT_SERVICE);
        RpcRequest rpcRequest = (RpcRequest) context.get(TolerantStrategyConstant.RPC_REQUEST);
        if (metaInfos == null || metaInfos.isEmpty()) {
            log.error("FailOverTolerantStrategy doTolerant metaInfos is empty");
            return null;
        }
        // 重试metaInfo之外的其他服务
        for (ServiceMetaInfo serviceMetaInfo : metaInfos) {
            if (serviceMetaInfo.equals(metaInfo)) {
                continue;
            }
            // 重试
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            try {
                return retryStrategy.doRetry(() -> {
                    return VertxTcpClient.doRequest(rpcRequest, metaInfo);
                });
            } catch (Exception ex) {
                // 如果重试再失败，继续重试下一个
                log.error("FailOverTolerantStrategy doTolerant retry fail");
            }
        }
        // 所有服务都重试失败
        throw new RuntimeException("FailOverTolerantStrategy doTolerant all retry fail");
    }
}
