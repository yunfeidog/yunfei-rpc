package com.yunfei.rpc.serializer;

import com.yunfei.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂 （单例）
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    // /**
    //  * 序列化方式 -> 序列化器 用于实现单例模式
    //  */
    // private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {{
    //     put(SerializerKeys.JDK, new JdkSerializer());
    //     put(SerializerKeys.JSON, new JsonSerializer());
    //     put(SerializerKeys.KRYO, new KryoSerializer());
    //     put(SerializerKeys.HESSIAN, new HessianSerializer());
    // }};

    /**
     * 默认序列化方式
     */
    // private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();


    /**
     * 获取序列化器
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        // return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
