package com.chua.common.support.resource.resource;

import com.chua.common.support.binary.ByteSourceArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * byte
 * @author CH
 */
public class ByteSourceResource implements Resource{
    private final String name;
    private final ByteSourceArray byteSourceArray;

    public ByteSourceResource(String name, ByteSourceArray byteSourceArray) {
        this.name = name;
        this.byteSourceArray = byteSourceArray;
    }

    @Override
    public InputStream openStream() throws IOException {
        return byteSourceArray.getInputStream();
    }

    @Override
    public String getUrlPath() {
        return null;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public long lastModified() {
        return 0;
    }
}
