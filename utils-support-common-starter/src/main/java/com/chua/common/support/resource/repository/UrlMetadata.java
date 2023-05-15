package com.chua.common.support.resource.repository;

import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.UrlUtils;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 元数据
 *
 * @author CH
 */
public final class UrlMetadata implements Metadata {
    private final URL url;
    private String contentType;

    public UrlMetadata(URL url) {
        this.url = url;
        if(null == url) {
            return;
        }
        this.contentType = UrlUtils.getContentType(url.toExternalForm());
    }

    @Override
    public URL toUrl() {
        return url;
    }

    @Override
    public InputStream openInputStream() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize() {
        return UrlUtils.size(url);
    }

    @Override
    public String getName() {
        if("file".equals(url.getProtocol())) {
            return new File(url.getFile()).getName();
        }

        return FileUtils.getName(url.getPath());
    }

    @Override
    public boolean isCompressFile() {
        if("file".equals(url.getProtocol())) {
            return FileUtils.isCompressFile(url.getFile());
        }

        return false;
    }

    @Override
    public boolean isImage() {
        if(Strings.isNullOrEmpty(contentType)) {
            return false;
        }
        return contentType.contains("image");
    }

    @Override
    public String getContentType() {
        if(Strings.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType;
    }

    @Override
    public String getType() {
        if(Strings.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType.substring(0, contentType.indexOf("/"));
    }

    @Override
    public String getSubType() {
        if(Strings.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType.substring(contentType.indexOf("/") + 1);
    }
}
