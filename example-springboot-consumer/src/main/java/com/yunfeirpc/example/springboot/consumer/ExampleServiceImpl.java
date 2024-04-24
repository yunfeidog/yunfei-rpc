package com.yunfeirpc.example.springboot.consumer;

import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.yunfeirpc.springboot.starter.annotation.YunRpcReference;
import org.springframework.stereotype.Service;

/**
 * @author houyunfei
 */
@Service
public class ExampleServiceImpl {
    @YunRpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("yunfei");
        User resultUser = userService.getUser(user);
        System.out.println("consumer get User:" + resultUser.getName());
    }
}
