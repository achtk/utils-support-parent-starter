package com.chua.common.support.resource.resource;

import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.chua.common.support.constant.CommonConstant.FILE;

/**
 * 系统文件
 * @author CH
 */
public class UrlResource implements Resource{

    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream openStream() throws IOException {
        return url.openStream();
    }

    @Override
    public String getUrlPath() {
        return null == url ? null : url.toExternalForm();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public long lastModified() {
        if(FILE.equals(url.getProtocol())) {
            return new File(url.getFile()).lastModified();
        }
        return UrlUtils.lastModified(url.toExternalForm());
    }
}
