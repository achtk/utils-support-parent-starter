package com.chua.common.support.lang.depends;

import com.chua.common.support.classloader.DelegateClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * 简单加载器
 *
 * @author CH
 */
public class SimplePathSurroundings implements Surroundings {

    private final ClassLoader classLoader;
    private final ClassLoader parent;

    public SimplePathSurroundings(URL[] urls, Map<String, byte[]> ext, ClassLoader parent) {
        this.parent = parent;
        this.classLoader = new DelegateClassLoader(ext, urls, parent);
    }

    private ClassLoader createClassLoader(URL[] urls) {
        return new URLClassLoader(urls, parent);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }


}
