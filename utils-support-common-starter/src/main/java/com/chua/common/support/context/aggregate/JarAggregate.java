package com.chua.common.support.context.aggregate;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.function.SafeFunction;
import com.chua.common.support.lang.depends.GrapeFileResolver;
import com.chua.common.support.lang.depends.Surroundings;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 聚合
 * @author CH
 */
public class JarAggregate implements Aggregate, InitializingAware {


    private final File file;
    private int order = 0;
    private String dependencyPath;
    private ClassLoader parent;
    private InputStream stream;
    private Surroundings surroundings;

    public JarAggregate(File file, int order, String dependencyPath, ClassLoader parent) {
        this.file = file;
        this.order = order;
        this.dependencyPath = dependencyPath;
        this.parent = parent;
        afterPropertiesSet();
    }

    public JarAggregate(Object file, String dependencyPath) {
        this(Converter.convertIfNecessary(file, File.class), 0, dependencyPath, null);
    }

    @Override
    public ClassLoader getClassLoader() {
        return surroundings.getClassLoader();
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public boolean contains(String name) {
        try {
            return null != getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("ALL")
    @Override
    public <T> Class<T> forName(String name, Class<T> targetType) {
        try {
            return (Class<T>) getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL getOriginal() {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() {
        if(FileUtils.isZip(FileUtils.getExtension(file.getName()))) {
            Zip zip = new Zip();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                zip.unFile(fileInputStream, (SafeFunction<FileMedia, Boolean>) fileMedia -> {
                    if(fileMedia.getName().contains(CommonConstant.POM)) {
                        JarAggregate.this.stream = fileMedia.getStream();
                        return true;
                    }

                    return false;
                }, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                this.surroundings = ServiceProvider.of(GrapeFileResolver.class).getNewExtension("default")
                        .resolve(stream, dependencyPath, parent, file);
            } finally {
                IoUtils.closeQuietly(stream);
            }
            return;
        }

        throw new UnsupportedOperationException();
    }
}
