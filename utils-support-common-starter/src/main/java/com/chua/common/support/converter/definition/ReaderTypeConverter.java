package com.chua.common.support.converter.definition;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Reader
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
public class ReaderTypeConverter implements TypeConverter<Reader> {

    private static final ReaderTypeConverter INSTANCE = new ReaderTypeConverter();

    @Override
    public Reader convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Reader) {
            return (Reader) value;
        }

        if (value instanceof File) {
            try {
                return new FileReader((File) value);
            } catch (FileNotFoundException ignored) {
            }
        }

        if (value instanceof FileDescriptor) {
            return new FileReader((FileDescriptor) value);
        }

        if (value instanceof Path) {
            try {
                return new FileReader(((Path) value).toFile());
            } catch (FileNotFoundException ignored) {
            }
        }


        if (value instanceof URL) {
            try {
                return INSTANCE.convert(((URL) value).openStream());
            } catch (Exception ignored) {
            }
        }

        if (value instanceof URI) {
            try {
                return INSTANCE.convert(((URI) value).toURL().openStream());
            } catch (Exception ignored) {
            }
        }

        if (value instanceof InputStream) {
            return new InputStreamReader((InputStream) value, UTF_8);
        }

        if (value instanceof String) {
            return new StringReader(value.toString());
        }

        if (value instanceof char[]) {
            return new CharArrayReader(((char[]) value));
        }

        if (value instanceof byte[]) {
            return new InputStreamReader(new ByteArrayInputStream(((byte[]) value)));
        }

        if (value instanceof PipedWriter) {
            try {
                return new PipedReader(((PipedWriter) value));
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    public Class<Reader> getType() {
        return Reader.class;
    }
}
