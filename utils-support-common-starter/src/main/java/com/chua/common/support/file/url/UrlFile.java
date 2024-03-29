package com.chua.common.support.file.url;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.utils.UrlUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * url
 *
 * @author CH
 */
@Spi("url")
public class UrlFile extends AbstractResourceFile {
    private final String urlPath;
    private URL url;

    public UrlFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
        this.urlPath = resourceConfiguration.getSourceUrl();
        try {
            this.url = new URL(urlPath);
        } catch (MalformedURLException ignored) {
        }
    }

    @Override
    public long size() {
        return UrlUtils.size(urlPath);
    }

    @Override
    public long lastModified() {
        return UrlUtils.lastModified(urlPath);
    }
}
