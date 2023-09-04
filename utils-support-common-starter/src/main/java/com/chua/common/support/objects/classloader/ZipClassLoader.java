package com.chua.common.support.objects.classloader;

import com.chua.common.support.utils.IoUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * zip类加载器
 *
 * @author CH
 * @since 2023/09/02
 */
@Slf4j
public class ZipClassLoader extends ClassLoader implements AutoCloseable {

    private final Map<String, Class<?>> nameAndType = new LinkedHashMap<>();

    final Map<String, byte[]> byteBufferMap1 = new ConcurrentHashMap<>();
    private final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
    private List<URL> urls;


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (nameAndType.containsKey(name)) {
            return nameAndType.get(name);
        }

        byte[] byteBuffer = byteBufferMap1.get(name);
        if (null == byteBuffer) {
            Class<?> aClass = null;
            try {
                aClass = parentClassLoader.loadClass(name);
            } catch (Exception ignored) {
            }
            if (null != aClass) {
                return aClass;
            }
        }

        Class<?> aClass = defineClassFromByteArray(name, byteBuffer);
        aClass = findInDepends(name, aClass);

        nameAndType.put(name, aClass);
        return aClass;
    }

    /**
     * 在依赖项中查找
     *
     * @param name   名称
     * @param aClass 类
     * @return {@link Class}<{@link ?}>
     */
    private Class<?> findInDepends(String name, Class<?> aClass) {
        if(null != aClass) {
            return aClass;
        }

        for (URL url : urls) {
            try {
                URL url1 = new URL(JAR_URL_PREFIX + url.toExternalForm() + JAR_URL_SEPARATOR + name.replace(".", "/") + SUFFIX_CLASS);
                try (InputStream is = url1.openStream()){
                    byte[] bytes = IoUtils.toByteArray(is);
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 从字节数组定义类
     *
     * @param name       名称
     * @param byteBuffer 字节缓冲区
     * @return {@link Class}<{@link ?}>
     */
    private Class<?> defineClassFromByteArray(String name, byte[] byteBuffer) {
        try {
            return super.defineClass(name, byteBuffer, 0, byteBuffer.length);
        } catch (Throwable ignored) {
        }

        return null;
    }


    public void add(String name, InputStream stream) {
        try (InputStream is = stream) {
            byteBufferMap1.put(name.replace("/", "."), IoUtils.toByteArray(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 依赖
     * @param urls 依赖
     */
    public void addDepends(List<URL> urls) {
        this.urls = urls;
    }

    @Override
    public void close() throws Exception {
        byteBufferMap1.clear();
        nameAndType.clear();
        urls.clear();
        System.gc();
    }
}
