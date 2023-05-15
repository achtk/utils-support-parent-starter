package com.chua.common.support.resource.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 资源
 * @author CH
 */
public interface Resource {
    /**
     * 初始化
     * @param path path
     * @return this
     */
    static Resource create(String path) {
        return new PathResource(path);
    }
    /**
     * 初始化
     * @param url url
     * @return this
     */
    static Resource create(URL url) {
        return new UrlResource(url);
    }
    /**
     * 初始化
     * @param file file
     * @return this
     */
    static Resource create(File file) {
        return new FileSystemResource(file);
    }
    /**
     * 流
     * @return 流
     * @throws IOException ex
     */
    InputStream openStream() throws IOException;

    /**
     * url
     * @return url
     */
    String getUrlPath();

    /**
     * url
     * @return url
     */
    URL getUrl();

    /**
     * 修改时间
     * @return 修改时间
     */
    long lastModified();
}
