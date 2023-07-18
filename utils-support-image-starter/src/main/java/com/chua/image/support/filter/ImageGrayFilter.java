package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * 灰度滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@Spi("gray")
@SpiOption("灰度滤镜")
public class ImageGrayFilter extends AbstractImageFilter {

    @Override
    public BufferedImage filter(BufferedImage image, BufferedImage dst) {
        // 转灰度图像
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        // 灰度
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(image, grayImage);
        //灰度图
        return grayImage;
    }
}
