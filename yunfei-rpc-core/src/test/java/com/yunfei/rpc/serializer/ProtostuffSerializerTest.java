package com.yunfei.rpc.serializer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProtostuffSerializerTest {

    @Test
    void serialize() throws Exception {
        Serializer serializer = new ProtostuffSerializer();
        byte[] bytes = serializer.serialize("hello");
        String str = serializer.deserialize(bytes, String.class);
        log.info("str: {}", str);
        assertEquals("hello", str);
    }

    @Test
    void deserialize() {
    }
}
