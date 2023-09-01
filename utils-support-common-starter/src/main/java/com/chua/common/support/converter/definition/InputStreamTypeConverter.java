package com.chua.common.support.converter.definition;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * InputStream
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
public class InputStreamTypeConverter implements TypeConverter<InputStream> {

    @Override
    public InputStream convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof BufferedImage) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write((RenderedImage) value, "jpg", byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (IOException ignored) {
            }
            return null;
        }

        if (value instanceof File) {
            try {
                return new FileInputStream((File) value);
            } catch (FileNotFoundException ignored) {
            }
        }

        if (value instanceof Path) {
            try {
                return new FileInputStream(((Path) value).toFile());
            } catch (FileNotFoundException ignored) {
            }
        }


        if (value instanceof URL) {
            try {
                return ((URL) value).openStream();
            } catch (Exception ignored) {
            }
        }

        if (value instanceof URI) {
            try {
                return ((URI) value).toURL().openStream();
            } catch (Exception ignored) {
            }
        }

        if (value instanceof FileDescriptor) {
            return new FileInputStream((FileDescriptor) value);
        }

        if (value instanceof Reader) {
            Reader reader = (Reader) value;
            try {
                byte[] bytes = IoUtils.toByteArray(reader);
                return new ByteArrayInputStream(bytes);
            } catch (IOException ignored) {
            }
        }

        if (value instanceof String) {
            String s = value.toString();
            if(FileUtils.exist(s)) {
                try {
                    return new FileInputStream(new File(s));
                } catch (FileNotFoundException ignored) {
                }
            }
            try {
                return Files.newInputStream(Converter.convertIfNecessary(value, File.class).toPath());
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    public Class<InputStream> getType() {
        return InputStream.class;
    }
}
