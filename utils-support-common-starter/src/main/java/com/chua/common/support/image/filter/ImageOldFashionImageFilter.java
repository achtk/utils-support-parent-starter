package com.chua.common.support.image.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 复古
 *
 * @author CH
 */
public class ImageOldFashionImageFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        BufferedImage back = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                int pixelVal = src.getRGB(i, j);
                int red = (pixelVal >> 16) & 0xFF;
                int green = (pixelVal >> 8) & 0xFF;
                int blue = pixelVal & 0xFF;
                int r = (int) (0.393 * red + 0.469 * green + 0.049 * blue);
                int g = (int) (0.349 * red + 0.586 * green + 0.068 * blue);
                int b = (int) (0.272 * red + 0.534 * green + 0.031 * blue);
                Color color = new Color(r, g, b);
                back.setRGB(i, j, color.getRGB());
            }
        }

        return back;
    }
}
