package com.yunfei.rpc.spi;

import com.yunfei.rpc.serializer.Serializer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

class SpiLoaderTest {

    @Test
    void loadAll() {
    }

    @Test
    void load() {
        Map<String, Class<?>> classMap = SpiLoader.load(Serializer.class);
        for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Test
    void getInstance() {
        this.load();
        Object jdk = SpiLoader.getInstance(Serializer.class, "jdk");
        System.out.println(jdk);
    }

    public static void main(String[] args) throws Exception {
        // Class<?> aClass = Class.forName("com.yunfei.rpc.spi.SpiLoaderTest");
        // System.out.println(aClass.getName());

        ServiceLoader<Animal> animalServiceLoader = ServiceLoader.load(Animal.class);
        for (Animal animal : animalServiceLoader) {
            animal.eat("shit");
        }
    }
}
