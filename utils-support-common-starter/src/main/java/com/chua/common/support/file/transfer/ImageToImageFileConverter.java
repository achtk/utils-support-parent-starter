package com.chua.common.support.file.transfer;

import com.chua.common.support.function.Joiner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * image to image
 * @author CH
 */
public class ImageToImageFileConverter extends AbstractFileConverter{
    @Override
    public String target() {
        return Joiner.on(',').join(ImageIO.getWriterFormatNames());
    }

    @Override
    public String source() {
        return Joiner.on(',').join(ImageIO.getReaderFileSuffixes());
    }

    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        ImageIO.write(bufferedImage, suffix, outputStream);
    }
}
