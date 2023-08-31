package com.chua.common.support.image.filter;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.BufferedImageUtils;

import java.awt.image.BufferedImage;

/**
 * find edge
 *
 * @author CH
 */
@Spi("FindEdge")
@SpiOption("边缘检测滤镜")
public class ImageFindEdgeFilter extends AbstractImageFilter {

    /**
     * The horizontal Sobel operator filter is used for edge detection
     * <p>
     * This is a 3x3 filter:<br>
     * -1 -2 -1 <br>
     * 0  0  0 <br>
     * 1  2  1 <br>
     */
    public static final int[] SOBEL_X = new int[]{-1, -2, -1, 0, 0, 0, 1, 2, 1};
    /**
     * Vertical Sobel operator filters for edge detection
     * <p>
     * This is a 3x3 filter:<br>
     * -1  0  1 <br>
     * -2  0  2 <br>
     * -1  0  1 <br>
     */
    public static final int[] SOBEL_Y = new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int total = width * height;
        byte[][] output = new byte[3][total];

        int offset = 0;
        int x0 = SOBEL_X[0];
        int x1 = SOBEL_X[1];
        int x2 = SOBEL_X[2];
        int x3 = SOBEL_X[3];
        int x4 = SOBEL_X[4];
        int x5 = SOBEL_X[5];
        int x6 = SOBEL_X[6];
        int x7 = SOBEL_X[7];
        int x8 = SOBEL_X[8];

        int k0 = SOBEL_Y[0];
        int k1 = SOBEL_Y[1];
        int k2 = SOBEL_Y[2];
        int k3 = SOBEL_Y[3];
        int k4 = SOBEL_Y[4];
        int k5 = SOBEL_Y[5];
        int k6 = SOBEL_Y[6];
        int k7 = SOBEL_Y[7];
        int k8 = SOBEL_Y[8];

        int yr = 0, yg = 0, yb = 0;
        int xr = 0, xg = 0, xb = 0;
        int r = 0, g = 0, b = 0;
        for (int row = 1; row < height - 1; row++) {
            offset = row * width;
            for (int col = 1; col < width - 1; col++) {
                // red
                yr = k0 * (rArr[offset - width + col - 1] & 0xff) + k1 * (rArr[offset - width + col] & 0xff) + k2 * (rArr[offset - width + col + 1] & 0xff) + k3 * (rArr[offset + col - 1] & 0xff) + k4 * (rArr[offset + col] & 0xff) + k5 * (rArr[offset + col + 1] & 0xff) + k6 * (rArr[offset + width + col - 1] & 0xff) + k7 * (rArr[offset + width + col] & 0xff) + k8 * (rArr[offset + width + col + 1] & 0xff);

                xr = x0 * (rArr[offset - width + col - 1] & 0xff) + x1 * (rArr[offset - width + col] & 0xff) + x2 * (rArr[offset - width + col + 1] & 0xff) + x3 * (rArr[offset + col - 1] & 0xff) + x4 * (rArr[offset + col] & 0xff) + x5 * (rArr[offset + col + 1] & 0xff) + x6 * (rArr[offset + width + col - 1] & 0xff) + x7 * (rArr[offset + width + col] & 0xff) + x8 * (rArr[offset + width + col + 1] & 0xff);

                /* green */
                yg = k0 * (gArr[offset - width + col - 1] & 0xff) + k1 * (gArr[offset - width + col] & 0xff) + k2 * (gArr[offset - width + col + 1] & 0xff) + k3 * (gArr[offset + col - 1] & 0xff) + k4 * (gArr[offset + col] & 0xff) + k5 * (gArr[offset + col + 1] & 0xff) + k6 * (gArr[offset + width + col - 1] & 0xff) + k7 * (gArr[offset + width + col] & 0xff) + k8 * (gArr[offset + width + col + 1] & 0xff);

                xg = x0 * (gArr[offset - width + col - 1] & 0xff) + x1 * (gArr[offset - width + col] & 0xff) + x2 * (gArr[offset - width + col + 1] & 0xff) + x3 * (gArr[offset + col - 1] & 0xff) + x4 * (gArr[offset + col] & 0xff) + x5 * (gArr[offset + col + 1] & 0xff) + x6 * (gArr[offset + width + col - 1] & 0xff) + x7 * (gArr[offset + width + col] & 0xff) + x8 * (gArr[offset + width + col + 1] & 0xff);
                // blue
                yb = k0 * (bArr[offset - width + col - 1] & 0xff) + k1 * (bArr[offset - width + col] & 0xff) + k2 * (bArr[offset - width + col + 1] & 0xff) + k3 * (bArr[offset + col - 1] & 0xff) + k4 * (bArr[offset + col] & 0xff) + k5 * (bArr[offset + col + 1] & 0xff) + k6 * (bArr[offset + width + col - 1] & 0xff) + k7 * (bArr[offset + width + col] & 0xff) + k8 * (bArr[offset + width + col + 1] & 0xff);

                xb = x0 * (bArr[offset - width + col - 1] & 0xff) + x1 * (bArr[offset - width + col] & 0xff) + x2 * (bArr[offset - width + col + 1] & 0xff) + x3 * (bArr[offset + col - 1] & 0xff) + x4 * (bArr[offset + col] & 0xff) + x5 * (bArr[offset + col + 1] & 0xff) + x6 * (bArr[offset + width + col - 1] & 0xff) + x7 * (bArr[offset + width + col] & 0xff) + x8 * (bArr[offset + width + col + 1] & 0xff);

                // magnitude
                r = (int) Math.sqrt(yr * yr + xr * xr);
                g = (int) Math.sqrt(yg * yg + xg * xg);
                b = (int) Math.sqrt(yb * yb + xb * xb);

                // find edges
                output[0][offset + col] = (byte) BufferedImageUtils.clamp(r);
                output[1][offset + col] = (byte) BufferedImageUtils.clamp(g);
                output[2][offset + col] = (byte) BufferedImageUtils.clamp(b);

                double dy = (yr + yg + yb);
                double dx = (xr + xg + xb);
                double theta = Math.atan(dy / dx);

                // for next pixel
                yr = 0;
                yg = 0;
                yb = 0;
                xr = 0;
                xg = 0;
                xb = 0;
            }
        }
        putRgb(output[0], output[1], output[2]);
        return toBitmap();
    }
}
