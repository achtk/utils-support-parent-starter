package com.chua.common.support.converter.definition;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Path
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
public class PathTypeConverter implements TypeConverter<Path> {

    @Override
    public Path convert(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Path) {
            return (Path) value;
        }

        if (value instanceof File) {
            return ((File) value).toPath();
        }

        if (value instanceof String) {
            try {
                return Paths.get(value.toString());
            } catch (Exception ignored) {
            }
        }

        if (value instanceof URL) {
            try {
                return Paths.get(((URL) value).toURI());
            } catch (URISyntaxException ignored) {
            }
        }

        if (value instanceof URI) {
            return Paths.get((URI) value);
        }
        return null;
    }

    @Override
    public Class<Path> getType() {
        return Path.class;
    }
}
