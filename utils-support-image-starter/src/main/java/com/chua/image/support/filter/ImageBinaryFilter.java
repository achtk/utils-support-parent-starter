package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 二值化滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@Spi("Binary")
@SpiOption("二值化滤镜")
public class ImageBinaryFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage image, BufferedImage image1) {
        //获取图像的高
        int h = image.getHeight();
        //获取图像的宽
        int w = image.getWidth();
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = getGray(image.getRGB(x, y));
            }
        }

        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        int sw = 160;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (getAverageColor(gray, x, y, w, h) > sw) {
                    int max = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, max);
                } else {
                    int min = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, min);
                }
            }
        }
        return nbi;
    }

    /**
     * 相对灰度值
     *
     * @param gray   灰度
     * @param x      x
     * @param y      y
     * @param with   宽
     * @param height 高
     * @return 相对灰度值
     */
    public static int getAverageColor(int[][] gray, int x, int y, int with, int height) {
        int rs = gray[x][y]
                + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == height - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == height - 1 ? 255 : gray[x][y + 1])
                + (x == with - 1 ? 255 : gray[x + 1][y])
                + (x == with - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == with - 1 || y == height - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

    /**
     * 灰度值
     *
     * @param rgb rgb
     * @return 灰度值
     */
    public static int getGray(int rgb) {
        int r, g, b;
        Color c = new Color(rgb);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
        return (r + g + b) / 3;
    }
}
