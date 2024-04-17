package com.yunfei.example.service;

import com.yunfei.example.model.User;

public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 获取数字
     */
    default short getNumber() {
        return 1;
    }
}
