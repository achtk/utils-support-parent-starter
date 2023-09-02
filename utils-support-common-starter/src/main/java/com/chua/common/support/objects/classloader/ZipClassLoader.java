package com.chua.common.support.objects.classloader;

import com.chua.common.support.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zip类加载器
 *
 * @author CH
 * @since 2023/09/02
 */
public class ZipClassLoader extends ClassLoader {

    private final String path;
    private final Map<String, Class<?>> nameAndType = new ConcurrentHashMap<>();

    final Map<String, byte[]> byteBufferMap1 = new ConcurrentHashMap<>();
    private final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

    public ZipClassLoader(String path) {
        this.path = path;
    }

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
                nameAndType.put(name, aClass);
                return aClass;
            }
        }

        Class<?> aClass = super.defineClass(name, byteBuffer, 0, byteBuffer.length);
        nameAndType.put(name, aClass);
        return aClass;
    }

    public void add(String name, InputStream stream) {
        try (InputStream is = stream) {
            byteBufferMap1.put(name, IoUtils.toByteArray(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
