package com.chua.common.support.image.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 负片
 *
 * @author CH
 */
@SpiOption("负片")
@Spi("negative")
public class ImageNegativeImageFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        BufferedImage back = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                int pixelVal = src.getRGB(i, j);
                int red = (pixelVal >> 16) & 0xFF;
                int green = (pixelVal >> 8) & 0xFF;
                int blue = pixelVal & 0xFF;
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;
                Color color = new Color(red, green, blue);
                back.setRGB(i, j, color.getRGB());
            }
        }

        return back;
    }
}
