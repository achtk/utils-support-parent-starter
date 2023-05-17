package com.chua.common.support.image.filter;

import com.chua.common.support.utils.BufferedImageUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;

/**
 * sobel
 *
 * @author CH
 */
@AllArgsConstructor
@NoArgsConstructor
public class ImageSobelFilter extends AbstractImageFilter {

    public static int[] sobelY = new int[]{-1, -2, -1, 0, 0, 0, 1, 2, 1};
    public static int[] sobelX = new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
    private boolean xdirect = true;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int total = width * height;
        byte[][] output = new byte[3][total];

        int offset = 0;
        int k0 = 0, k1 = 0, k2 = 0;
        int k3 = 0, k4 = 0, k5 = 0;
        int k6 = 0, k7 = 0, k8 = 0;
        if (xdirect) {
            k0 = sobelX[0];
            k1 = sobelX[1];
            k2 = sobelX[2];
            k3 = sobelX[3];
            k4 = sobelX[4];
            k5 = sobelX[5];
            k6 = sobelX[6];
            k7 = sobelX[7];
            k8 = sobelX[8];
        } else {
            k0 = sobelY[0];
            k1 = sobelY[1];
            k2 = sobelY[2];
            k3 = sobelY[3];
            k4 = sobelY[4];
            k5 = sobelY[5];
            k6 = sobelY[6];
            k7 = sobelY[7];
            k8 = sobelY[8];
        }

        int sr = 0, sg = 0, sb = 0;
        int r = 0, g = 0, b = 0;
        for (int row = 1; row < height - 1; row++) {
            offset = row * width;
            for (int col = 1; col < width - 1; col++) {
                // red
                sr = k0 * (rArr[offset - width + col - 1] & 0xff)
                        + k1 * (rArr[offset - width + col] & 0xff)
                        + k2 * (rArr[offset - width + col + 1] & 0xff)
                        + k3 * (rArr[offset + col - 1] & 0xff)
                        + k4 * (rArr[offset + col] & 0xff)
                        + k5 * (rArr[offset + col + 1] & 0xff)
                        + k6 * (rArr[offset + width + col - 1] & 0xff)
                        + k7 * (rArr[offset + width + col] & 0xff)
                        + k8 * (rArr[offset + width + col + 1] & 0xff);
                // green
                sg = k0 * (gArr[offset - width + col - 1] & 0xff)
                        + k1 * (gArr[offset - width + col] & 0xff)
                        + k2 * (gArr[offset - width + col + 1] & 0xff)
                        + k3 * (gArr[offset + col - 1] & 0xff)
                        + k4 * (gArr[offset + col] & 0xff)
                        + k5 * (gArr[offset + col + 1] & 0xff)
                        + k6 * (gArr[offset + width + col - 1] & 0xff)
                        + k7 * (gArr[offset + width + col] & 0xff)
                        + k8 * (gArr[offset + width + col + 1] & 0xff);
                // blue
                sb = k0 * (bArr[offset - width + col - 1] & 0xff)
                        + k1 * (bArr[offset - width + col] & 0xff)
                        + k2 * (bArr[offset - width + col + 1] & 0xff)
                        + k3 * (bArr[offset + col - 1] & 0xff)
                        + k4 * (bArr[offset + col] & 0xff)
                        + k5 * (bArr[offset + col + 1] & 0xff)
                        + k6 * (bArr[offset + width + col - 1] & 0xff)
                        + k7 * (bArr[offset + width + col] & 0xff)
                        + k8 * (bArr[offset + width + col + 1] & 0xff);
                r = sr;
                g = sg;
                b = sb;
                output[0][offset + col] = (byte) BufferedImageUtils.clamp(r);
                output[1][offset + col] = (byte) BufferedImageUtils.clamp(g);
                output[2][offset + col] = (byte) BufferedImageUtils.clamp(b);

                // for next pixel
                sr = 0;
                sg = 0;
                sb = 0;
            }
        }

        putRgb(output[0], output[1], output[2]);
        return toBitmap();
    }
}
