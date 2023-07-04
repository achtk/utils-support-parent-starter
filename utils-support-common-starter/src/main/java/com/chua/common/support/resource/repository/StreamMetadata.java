package com.chua.common.support.resource.repository;

import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 元数据
 *
 * @author CH
 */
public final class StreamMetadata implements Metadata {
    private final ByteSourceArray byteSourceArray;
    private final String contentType;

    public StreamMetadata(InputStream stream, String contentType) {
        this.contentType = contentType;
        try {
            this.byteSourceArray = new ByteSourceArray(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL toUrl() {
        return null;
    }

    @Override
    public InputStream openInputStream() {
        return byteSourceArray.getInputStream();
    }

    @Override
    public long getSize() {
        return byteSourceArray.getLength();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isCompressFile() {
        return false;
    }

    @Override
    public boolean isImage() {
        if(StringUtils.isNullOrEmpty(contentType)) {
            return false;
        }
        return contentType.contains("image");
    }

    @Override
    public String getContentType() {
        if(StringUtils.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType;
    }

    @Override
    public String getType() {
        if(StringUtils.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType.substring(0, contentType.indexOf("/"));
    }

    @Override
    public String getSubType() {
        if(StringUtils.isNullOrEmpty(contentType)) {
            return null;
        }
        return contentType.substring(contentType.indexOf("/") + 1);
    }
}
