package com.yunfei.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.yunfei.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器 （支持键值对映射）
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名=>（key=>实现类）
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存 避免重放new 类路径=> 对象实例 ，单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{
            RPC_SYSTEM_SPI_DIR,
            RPC_CUSTOM_SPI_DIR,
    };

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有的SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 加载某个类型
     *
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的SPI", loadClass.getName());
        // 扫描路径，用户自定义的SPI 优先级高于系统SPI
        HashMap<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            log.info("扫描路径为 {}", scanDir + loadClass.getName());
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length < 2) {
                            log.error("SPI配置文件格式错误");
                            continue;
                        }
                        String key = split[0];
                        String className = split[1];
                        log.info("加载 {} SPI配置文件 key={} className={}", scanDir.equals(RPC_CUSTOM_SPI_DIR) ? "自定义" : "系统", key, className);
                        keyClassMap.put(key, Class.forName(className));
                    }
                } catch (Exception e) {
                    log.error("加载SPI配置文件失败", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取某个类型的实例
     *
     * @param tClass
     * @param key
     * @param <T>    类型
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载%s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s不存在 key= %s", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String format = String.format("实例化 %s 失败", implClassName);
                throw new RuntimeException(format, e);
            }
        }
        return (T) instanceCache.get(implClassName);

    }
}
