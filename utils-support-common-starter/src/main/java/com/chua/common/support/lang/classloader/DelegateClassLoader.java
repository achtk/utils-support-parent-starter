package com.chua.common.support.lang.classloader;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;

import static com.chua.common.support.constant.CommonConstant.JAR_URL_SEPARATOR;

/**
 * url
 * @author CH
 */
@Slf4j
public class DelegateClassLoader extends URLClassLoader implements AutoCloseable {
    private Map<String, byte[]> urlsBytes;
    private URL[] urls1;
    private final Map<String, Value<Class<?>>> value = new ConcurrentReferenceHashMap<>(512);


    public DelegateClassLoader(Map<String, byte[]> urlsBytes, URL[] urls1, ClassLoader parent) {
        super(new URL[0], parent);
        this.urlsBytes = urlsBytes;
        this.urls1 = urls1;
    }


    /**
     * 加载的所有类
     *
     * @return 加载的所有类
     */
    public Class<?>[] listClass() {
        return urlsBytes.keySet().stream().map(it -> {
            try {
                return this.loadClass(it, false);
            } catch (Throwable e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage());
                }
                return null;
            }
        }).filter(Objects::nonNull).toArray(Class<?>[]::new);
    }


    @Override
    public void close() throws IOException {
        super.close();
        urlsBytes.clear();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (value.containsKey(name)) {
            return value.get(name).getValue();
        }

        Class<?> aClass = null;
        byte[] byteBuffer = urlsBytes.get(name);
        if (null == byteBuffer) {
            byteBuffer = createByteBuffer(name);
            if (null == byteBuffer) {
                value.put(name, Value.of(null));
                return null;
            }
        }

        try {
            byte[] copy = new byte[byteBuffer.length];
            System.arraycopy(byteBuffer, 0, copy, 0, copy.length);
            aClass = defineClass(name, copy, 0, copy.length);
        } catch (Throwable e) {
            try {
                aClass = getParent().loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }

        value.put(name, Value.of(aClass));
        return aClass;
    }

    private byte[] createByteBuffer(String name) {
        for (URL url : urls1) {
            String s = FileUtils.getAbsolutePath(url.toExternalForm()) + JAR_URL_SEPARATOR + name.replace(".", "/");
            try (InputStream inputStream = new URL("jar:file:" + s + ".class").openStream();){
                return IoUtils.toByteArray(inputStream);
            } catch (Exception ignored) {
            }

        }
        return new byte[0];
    }
}
