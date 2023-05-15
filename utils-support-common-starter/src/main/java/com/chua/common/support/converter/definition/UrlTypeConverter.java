package com.chua.common.support.converter.definition;

import com.chua.common.support.utils.RegexUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Base64;

/**
 * URL类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/26
 */
public class UrlTypeConverter implements TypeConverter<URL> {

    @Override
    public URL convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof URL) {
            return (URL) value;
        }

        if (value instanceof File) {
            try {
                return ((File) value).toURI().toURL();
            } catch (MalformedURLException ignored) {
            }
        }

        if (value instanceof Path) {
            try {
                return ((Path) value).toUri().toURL();
            } catch (MalformedURLException ignored) {
            }
        }

        if (value instanceof URI) {
            try {
                return ((URI) value).toURL();
            } catch (MalformedURLException ignored) {
            }
        }

        if (value instanceof String) {
            String str = value.toString();

            try {
                File file = new File(str);
                if (file.exists()) {
                    return file.toURI().toURL();
                }
            } catch (MalformedURLException e1) {
                try {
                    return new URL(value.toString());
                } catch (MalformedURLException ignored) {
                }
            }
        }

        return null;
    }

    @Override
    public Class<URL> getType() {
        return URL.class;
    }
}
