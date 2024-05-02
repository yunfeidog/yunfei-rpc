package com.yunfei.rpc.loadbalancer;

import com.yunfei.rpc.model.ServiceMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author houyunfei
 * 加权轮询负载均衡
 */
public class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    private AtomicInteger num = new AtomicInteger(0); // 计数器  用于轮询

    @Data
    @AllArgsConstructor
    public static class Weight {
        private ServiceMetaInfo info;
        private int weight;
        private int currentWeight;
    }

    public static Map<ServiceMetaInfo, Weight> currentWeightMap = new HashMap();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int totalWeight = 0; // 总权重
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            totalWeight += serviceMetaInfo.getWeight();
        }

        if (currentWeightMap.isEmpty()) {
            for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
                // 初始化权重  currentWeight = 0
                currentWeightMap.put(serviceMetaInfo, new Weight(serviceMetaInfo, serviceMetaInfo.getWeight(), 0));
            }

        }

        // 将权重值加上动态权重
        for (Weight weight : currentWeightMap.values()) {
            weight.setCurrentWeight(weight.getCurrentWeight() + weight.getWeight());
        }


        // 选出最大的权重
        Weight maxWeight = null;
        for (Weight weight : currentWeightMap.values()) {
            if (maxWeight == null || weight.getCurrentWeight() > maxWeight.getCurrentWeight()) {
                maxWeight = weight;
            }
        }

        // 将最大的权重减去总权重
        maxWeight.setCurrentWeight(maxWeight.getCurrentWeight() - totalWeight);
        // 返回最大权重对应的服务
        return maxWeight.getInfo();
    }
}

// /**
//  * @author houyunfei
//  * 加权轮询负载均衡
//  */
// public class WeightedRoundRobinLoadBalancer implements LoadBalancer {
//
//     private AtomicInteger num = new AtomicInteger(0); // 计数器  用于轮询
//
//     @Override
//     public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
//         int totalWeight = 0; // 总权重
//         for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
//             totalWeight += serviceMetaInfo.getWeight();
//         }
//         int pos = num.getAndIncrement() % totalWeight; // 取模
//         for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
//             int weight = serviceMetaInfo.getWeight();
//             if (pos < weight) { // 如果当前位置小于权重值，返回当前服务
//                 return serviceMetaInfo;
//             }
//             pos -= weight; // 否则减去权重值，继续循环
//         }
//         return null;
//     }
// }

/**
 * @author houyunfei
 * 加权轮询负载均衡
 */
/*

public class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    private AtomicInteger currentIndex = new AtomicInteger(0);


    private static List<ServiceMetaInfo> list;

    void init(List<ServiceMetaInfo> serviceMetaInfoList) {
        list = new ArrayList<>();
        // 构建
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            int weight = serviceMetaInfo.getWeight();
            // 有多少个权重就添加多少个
            for (int i = 0; i < weight; i++) {
                list.add(serviceMetaInfo);
            }
        }
    }


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        ServiceMetaInfo serviceMetaInfo = list.get(currentIndex.getAndIncrement() % list.size());
        return serviceMetaInfo;
    }
}
 */
