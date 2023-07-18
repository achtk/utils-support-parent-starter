package com.chua.image.support.filter;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.image.AsciiImage;
import com.chua.common.support.image.converter.AsciiToImageConverter;
import com.chua.common.support.image.strategy.ColorSquareErrorFitStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 文本滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@Spi("Txt")
@SpiOption("文本滤镜")
public class ImageTxtFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage image, BufferedImage image1) {
        // initialize caches
        AsciiImage cache = AsciiImage.create(new Font("Courier", Font.BOLD, 6));
        // initialize converters
        AsciiToImageConverter imageConverter = new AsciiToImageConverter(cache, new ColorSquareErrorFitStrategy());
        // 转灰度图像
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        new ImageGrayscaleFilter().filter(image, grayImage);
        return imageConverter.convertImage(grayImage);
    }

}
