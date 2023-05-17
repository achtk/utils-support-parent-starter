package com.chua.common.support.file.image;

import com.chua.common.support.exception.NotSupportedException;
import com.chua.common.support.file.resource.*;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.StringUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * @author CH
 */
@Spi("image")
public class ImageResourceFile extends AbstractResourceFile implements ImageFile {

    public ImageResourceFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }


    @Override
    public BufferedImage toBufferedImage() throws IOException {
        return ImageIO.read(openInputStream());
    }

    @Override
    public void toCompress(float quality, OutputStream outputStream) {
        BufferedImage image = null;
        try {
            image = toBufferedImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Iterator<ImageWriter> iter = null;
        try {
            iter = ImageIO.getImageWritersByFormatName(resourceConfiguration.getSubtype());
        } catch (Exception e) {
            throw new NotSupportedException("不支持压缩");
        }
        // 得到writer
        ImageWriter writer = (ImageWriter) iter.next();
        // 得到指定writer的输出参数设置(ImageWriteParam )
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        // 设置可否压缩
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // 设置压缩质量参数
        iwp.setCompressionQuality(quality / 100f);
        iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
        ColorModel colorModel = ColorModel.getRGBdefault();
        // 指定压缩时使用的色彩模式
        iwp.setDestinationType(
                new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
        // 开始打包图片，写入byte[]
        // 取得内存输出流
        IIOImage iIamge = new IIOImage(image, null, null);
        // 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput
        // 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput
        try {
            writer.setOutput(ImageIO.createImageOutputStream(outputStream));
            writer.write(null, iIamge, iwp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExifFile toExifFile(String type) throws IOException {
        return ServiceProvider.of(ExifFile.class).getNewExtension(StringUtils.defaultString(type, "image"), resourceConfiguration);
    }

    @Override
    public ImageEditFile toEditFile(String type) throws IOException {
        return ServiceProvider.of(ImageEditFile.class).getNewExtension(StringUtils.defaultString(type, "image"), resourceConfiguration);
    }
}
