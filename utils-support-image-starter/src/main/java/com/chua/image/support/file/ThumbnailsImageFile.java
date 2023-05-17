package com.chua.image.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ExifFile;
import com.chua.common.support.file.ImageEditFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.resource.ResourceConfiguration;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片文件
 *
 * @author CH
 */
@Spi("image")
public class ThumbnailsImageFile extends AbstractResourceFile implements ImageEditFile {
    private final Thumbnails.Builder<BufferedImage> thumbnails;
    private final ExifFile exifFile;


    public ThumbnailsImageFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
        try (InputStream is = openInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(is);
            this.thumbnails = Thumbnails.of(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.exifFile = new ImageExifFile(resourceConfiguration);
    }

    @Override
    public ExifFile getExifFile() {
        return exifFile;
    }

    @Override
    public ImageEditFile size(int width, int height) {
        thumbnails.size(width, height);
        return this;
    }

    @Override
    public ImageEditFile keepAspectRatio(boolean keepAspectRatio) {
        thumbnails.keepAspectRatio(keepAspectRatio);
        return this;
    }

    @Override
    public ImageEditFile outputQuality(float outputQuality) {
        thumbnails.outputQuality(outputQuality);
        return this;
    }

    @Override
    public ImageEditFile useOriginalFormat() {
        thumbnails.useOriginalFormat();
        return this;
    }

    @Override
    public ImageEditFile scale(float scale) {
        thumbnails.scale(scale);
        return this;
    }

    @Override
    public ImageEditFile rotate(double angle) {
        thumbnails.rotate(angle);
        return this;
    }

    @Override
    public ImageEditFile sourceRegion(Rectangle rectangle) {
        thumbnails.sourceRegion(rectangle);
        return this;
    }

    @Override
    public ImageEditFile addFilter(ImageFilter imageFilter) {
        thumbnails.addFilter(img -> {
            try {
                return imageFilter.converter(img);
            } catch (IOException e) {
                return img;
            }
        });
        return this;
    }

    @Override
    public ImageEditFile watermark(BufferedImage image) {
        thumbnails.watermark(image);
        return this;
    }

    @Override
    public ImageEditFile watermark(Point point, BufferedImage image, float opacity) {
        thumbnails.watermark(new Coordinate(point.x, point.y), image, opacity);
        return this;
    }

}
