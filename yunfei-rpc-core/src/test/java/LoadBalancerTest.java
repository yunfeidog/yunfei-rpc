import com.yunfei.rpc.loadbalancer.ConsistentHashLoadBalancer;
import com.yunfei.rpc.loadbalancer.LoadBalancer;
import com.yunfei.rpc.loadbalancer.RandomLoadBalancer;
import com.yunfei.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.yunfei.rpc.model.ServiceMetaInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoadBalancerTest {
    // 更换不同的负载均衡策略 测试
    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void select() {
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "sayHello");

        // 服务提供者列表
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);

        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("myService");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("localhost");
        serviceMetaInfo2.setServicePort(1235);

        for (int i = 0; i < 3; i++) {
            List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo2, serviceMetaInfo);
            ServiceMetaInfo metaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println("第" + i + "次选择的服务提供者：" + metaInfo);
        }


    }
}
