package com.yunfei.example.provider;

import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("UserServiceImpl.getUser username=" + user.getName());
        return user;
    }
}
