package com.yunfei.rpc.serializer;

import java.io.IOException;

/**
 * 序列化接口
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 待序列化的对象
     * @param <T> 对象类型
     * @return 序列化后的字节数组
     * @throws IOException 序列化异常
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @param clazz 对象类型
     * @param <T>   对象类型
     * @return 反序列化后的对象
     * @throws IOException 反序列化异常
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
