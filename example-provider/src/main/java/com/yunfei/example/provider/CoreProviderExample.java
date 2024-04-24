package com.yunfei.example.provider;

import com.yunfei.example.service.UserService;
import com.yunfei.rpc.bootstrap.ProviderBootstrap;
import com.yunfei.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;


public class CoreProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfos.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfos);

    }
}
