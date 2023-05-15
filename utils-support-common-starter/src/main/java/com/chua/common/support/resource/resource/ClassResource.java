package com.chua.common.support.resource.resource;

import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 类资源
 * @author CH
 */
@EqualsAndHashCode
public class ClassResource implements Resource{


    private final ClassLoader classLoader;

    private final String type;

    public ClassResource(ClassLoader classLoader, String type) {
        this.classLoader = classLoader;
        this.type = type;
    }

    @Override
    public InputStream openStream() throws IOException {
        return getUrl().openStream();
    }

    @Override
    public String getUrlPath() {
        return getUrl().toExternalForm();
    }

    @Override
    public URL getUrl() {
        return loader().getProtectionDomain().getCodeSource().getLocation();
    }

    @Override
    public long lastModified() {
        return 0;
    }

    /**
     * 加载器对象
     * @return 对象
     */
    public Class<?> loader() {
        try {
            return classLoader.loadClass(type);
        } catch (Throwable e) {
            return null;
        }
    }
}
