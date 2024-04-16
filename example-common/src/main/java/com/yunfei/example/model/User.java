package com.yunfei.example.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类、注意一定要实现序列化接口 因为要进行网络传输
 */
@Data
public class User implements Serializable {
    private String name;
}
