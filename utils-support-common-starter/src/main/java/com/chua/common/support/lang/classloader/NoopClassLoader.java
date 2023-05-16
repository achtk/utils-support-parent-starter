package com.chua.common.support.lang.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * url
 * @author CH
 */
public class NoopClassLoader extends URLClassLoader {
    public NoopClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
