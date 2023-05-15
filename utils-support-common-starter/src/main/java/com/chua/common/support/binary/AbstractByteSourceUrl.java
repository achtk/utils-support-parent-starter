package com.chua.common.support.binary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * ByteSourceArray
 *
 * @author CH
 * @since 2022-02-11
 */
public class AbstractByteSourceUrl extends AbstractByteSourceInputStream {
    private URL url;

    public AbstractByteSourceUrl(URL url) throws IOException {
        this(url.openStream(), url.toExternalForm(), url);
    }

    public AbstractByteSourceUrl(InputStream is, String filename, URL url) {
        super(is, filename);
        this.url = url;
    }

    public AbstractByteSourceUrl(InputStream is, URL url) {
        super(is);
        this.url = url;
    }
}