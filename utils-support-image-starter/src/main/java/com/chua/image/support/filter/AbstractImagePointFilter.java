package com.chua.image.support.filter;

import com.chua.common.support.constant.NumberConstant;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * 点过滤器的抽象超类。界面与旧的RGBImageFilter相同。
 *
 * @author CH
 */
public abstract class AbstractImagePointFilter extends AbstractImageFilter {

    protected boolean canFilterIndexColorModel = false;

    public static final int MAX_256 = NumberConstant.MAX_256;
    public static final int MAX_128 = NumberConstant.MAX_128;
    public static final int MAX_255 = NumberConstant.MAX_255;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        int type = src.getType();
        WritableRaster srcRaster = src.getRaster();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        WritableRaster dstRaster = dst.getRaster();

        setDimensions(width, height);

        int[] inPixels = new int[width];
        for (int y = 0; y < height; y++) {
            // We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
            if (type == BufferedImage.TYPE_INT_ARGB) {
                srcRaster.getDataElements(0, y, width, 1, inPixels);
                for (int x = 0; x < width; x++) {
                    inPixels[x] = filterRgb(x, y, inPixels[x]);
                }
                dstRaster.setDataElements(0, y, width, 1, inPixels);
            } else {
                src.getRGB(0, y, width, 1, inPixels, 0, width);
                for (int x = 0; x < width; x++) {
                    inPixels[x] = filterRgb(x, y, inPixels[x]);
                }
                dst.setRGB(0, y, width, 1, inPixels, 0, width);
            }
        }

        return dst;
    }

    /**
     * 过滤
     *
     * @param x   x
     * @param y   y
     * @param rgb rgb
     * @return 结果
     */
    public abstract int filterRgb(int x, int y, int rgb);

    /**
     * 设置尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setDimensions(int width, int height) {
    }
}
