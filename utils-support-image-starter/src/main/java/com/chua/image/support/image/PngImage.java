package com.chua.image.support.image;

import com.chua.common.support.image.image.AbstractImageWriter;
import com.chua.common.support.image.image.CompressImage;
import com.chua.common.support.image.image.Image;
import com.chua.common.support.annotations.Spi;
import com.idrsolutions.image.png.PngCompressor;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Compression;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.function.Consumer;

/**
 * png 图像
 * @author CH
 */
@Spi({"png"})
public class PngImage extends AbstractImageWriter implements Image, CompressImage {

    private final URL imageUrl;

    public PngImage(URL imageUrl) {
        this.imageUrl = imageUrl;
        try {
            parseFrame(null);
        } catch (IOException ignored) {
        }
    }

    @Override
    public String getType() {
        return "png";
    }

    @Override
    public BufferedImage getBufferedImage() throws IOException {
        return ImageIO.read(getInputStream());
    }

    @Override
    public void toCompress(float quality, OutputStream outputStream) throws IOException {
        PngCompressor.compress(getInputStream(), outputStream);
    }

    @Override
    public void parseFrame(Consumer<BufferedImage> consumer) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageUrl);
        addFrame(bufferedImage);
        if(null != consumer) {
            consumer.accept(bufferedImage);
        }
    }
}
