package com.yunfeirpc.example.springboot.provider;

import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.yunfeirpc.springboot.starter.annotation.YunRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author houyunfei
 */
@Slf4j
@Service
@YunRpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("provider received: " + user);
        return user;
    }
}
