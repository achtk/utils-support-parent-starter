package com.chua.common.support.binary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.chua.common.support.constant.CommonConstant.FILE;

/**
 * ByteSourceArray
 *
 * @author CH
 * @since 2022-02-11
 */
public class ByteSourceUrl extends ByteSourceInputStream {
    private URL url;

    /**
     * 是否是文件
     *
     * @return 是否是文件
     */
    public boolean isFile() {
        return null != url && FILE.equals(url.getProtocol());
    }

    /**
     * 获取文件
     *
     * @return 文件
     */
    public String getFile() {
        return isFile() ? url.getFile() : null;
    }

    public ByteSourceUrl(URL url) throws IOException {
        this(url.openStream(), url.toExternalForm(), url);
    }

    public ByteSourceUrl(InputStream is, String filename, URL url) {
        super(is, filename);
        this.url = url;
    }

    public ByteSourceUrl(InputStream is, URL url) {
        super(is);
        this.url = url;
    }
}