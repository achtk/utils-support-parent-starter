package com.chua.common.support.image.filter;


import com.chua.common.support.utils.BufferedImageUtils;

import java.awt.image.BufferedImage;

/**
 * usm
 *
 * @author CH
 */
public class ImageUsmFilterImage extends ImageGaussianBlurFilter {

    private double weight;

    public ImageUsmFilterImage() {
        this.weight = 0.6;
    }

    public ImageUsmFilterImage(double weight) {
        this.weight = weight;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        initial(src);
        int total = width * height;
        byte[] r1 = new byte[total];
        byte[] g1 = new byte[total];
        byte[] b1 = new byte[total];
        System.arraycopy(rArr, 0, r1, 0, total);
        System.arraycopy(gArr, 0, g1, 0, total);
        System.arraycopy(bArr, 0, b1, 0, total);
        byte[][] output = new byte[3][total];
        // 高斯模糊
        super.filter(src, dst);
        int r = 0;
        int g = 0;
        int b = 0;

        int r11 = 0;
        int g11 = 0;
        int b11 = 0;

        int r2 = 0;
        int g2 = 0;
        int b2 = 0;

        for (int i = 0; i < total; i++) {
            r11 = r1[i] & 0xff;
            g11 = g1[i] & 0xff;
            b11 = b1[i] & 0xff;

            r2 = rArr[i] & 0xff;
            g2 = gArr[i] & 0xff;
            b2 = bArr[i] & 0xff;

            r = (int) ((r11 - weight * r2) / (1 - weight));
            g = (int) ((g11 - weight * g2) / (1 - weight));
            b = (int) ((b11 - weight * b2) / (1 - weight));

            output[0][i] = (byte) BufferedImageUtils.clamp(r);
            output[1][i] = (byte) BufferedImageUtils.clamp(g);
            output[2][i] = (byte) BufferedImageUtils.clamp(b);
        }

        putRgb(output[0], output[1], output[2]);
        return toBitmap();
    }


}
