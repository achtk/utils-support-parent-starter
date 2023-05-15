package com.chua.common.support.resource.resource;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

/**
 * 系统文件
 * @author CH
 */
public class PathResource implements Resource{

    private final String path;

    public PathResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream openStream() throws IOException {
        try {
            return new URL(path).openStream();
        } catch (MalformedURLException ignored) {
        }

        File file = Converter.convertIfNecessary(path, File.class);
        if(null != file) {
            try {
                return Files.newInputStream(file.toPath());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public String getUrlPath() {
        return path;
    }

    @Override
    public URL getUrl() {
        try {
            return new URL(path);
        } catch (MalformedURLException ignored) {
        }

        File file = Converter.convertIfNecessary(path, File.class);
        if(null != file) {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public long lastModified() {
        long lastModified = UrlUtils.lastModified(path);
        if(lastModified > 0) {
            return lastModified;
        }

        File file = Converter.convertIfNecessary(path, File.class);
        if(null != file) {
            return file.lastModified();
        }

        return -1;
    }
}
