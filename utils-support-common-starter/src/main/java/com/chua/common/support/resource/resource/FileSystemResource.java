package com.chua.common.support.resource.resource;

import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode
public class FileSystemResource implements Resource{

    private final File file;


    public FileSystemResource(File file) {
        this.file = file;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }

    @Override
    public String getUrlPath() {
        return file.getAbsolutePath();
    }

    @Override
    public URL getUrl() {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }
}
