package com.chua.common.support.converter.definition;

import com.chua.common.support.converter.Converter;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * BufferedImage
 *
 * @author CH
 */
public class BufferedImageTypeConverter implements TypeConverter<BufferedImage> {
    @Override
    public Class<BufferedImage> getType() {
        return BufferedImage.class;
    }

    @Override
    public BufferedImage convert(Object value) {
        try {
            if(value instanceof File) {
                return ImageIO.read((File) value);
            }

            if(value instanceof Path) {
                return ImageIO.read(((Path) value).toFile());
            }

            if(value instanceof String) {
                try {
                    return ImageIO.read(new File(value.toString()));
                } catch (IOException ignored) {
                }
                URL url = Converter.convertIfNecessary(value, URL.class);
                if(null != url) {
                    return ImageIO.read(url);
                }

                return null;
            }

            if(value instanceof InputStream) {
                return ImageIO.read((InputStream) value);
            }

            if(value instanceof ImageInputStream) {
                return ImageIO.read((ImageInputStream) value);
            }

            if(value instanceof URL) {
                return ImageIO.read((URL) value);
            }



            if(value instanceof byte[]) {
                try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream((byte[]) value)) {
                    return ImageIO.read(arrayInputStream);
                }
            }
        } catch (IOException ignored) {
        }

        return null;
    }
}
