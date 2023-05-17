package com.chua.image.support.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

/**
 * 盐化滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ImageSaltFilter extends AbstractImageFilter {

    private static final float SNR = 0.5f;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int[] inPixels = new int[width * height];
        getRgb(src, 0, 0, width, height, inPixels);

        int index = 0;
        int size = (int) (inPixels.length * (1 - SNR));

        for (int i = 0; i < size; i++) {
            int row = (int) (Math.random() * (double) height);
            int col = (int) (Math.random() * (double) width);
            index = row * width + col;
            inPixels[index] = (255 << 24) | (255 << 16) | (255 << 8) | 255;
        }

        setRgb(dst, 0, 0, width, height, inPixels);
        return dst;
    }
}
