package com.chua.common.support.image.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 明亮
 *
 * @author CH
 */
@Spi("Bright")
@SpiOption("明亮滤镜")
public class ImageBrightImageFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        BufferedImage back = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                int pixelVal = src.getRGB(i, j);
                int red = (pixelVal >> 16) & 0xFF;
                int green = (pixelVal >> 8) & 0xFF;
                int blue = pixelVal & 0xFF;
                red = Math.min(255, red + 10);
                blue = Math.min(255, blue + 10);
                green = Math.min(255, green + 10);
                Color color = new Color(red, green, blue);
                back.setRGB(i, j, color.getRGB());
            }
        }

        return back;
    }
}
