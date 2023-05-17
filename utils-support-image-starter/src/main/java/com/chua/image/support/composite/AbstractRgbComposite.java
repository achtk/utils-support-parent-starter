package com.chua.image.support.composite;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static com.chua.common.support.constant.NumberConstant.ONE_FLOAT;
import static com.chua.common.support.constant.NumberConstant.ZERO_FLOAT;

/**
 * RGB复合
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public abstract class AbstractRgbComposite implements Composite {

    protected float extraAlpha;

    /**
     * 初始化
     */
    public AbstractRgbComposite() {
        this(1.0f);
    }

    /**
     * 初始化
     *
     * @param alpha alpha
     */
    public AbstractRgbComposite(float alpha) {
        if (alpha < ZERO_FLOAT || alpha > ONE_FLOAT) {
            throw new IllegalArgumentException("RgbComposite: alpha must be between 0 and 1");
        }
        this.extraAlpha = alpha;
    }

    /**
     * 获取alpha
     *
     * @return alpha
     */
    public float getAlpha() {
        return extraAlpha;
    }

    /**
     * 内容化
     */
    public abstract static class AbstractRgbCompositeContext implements CompositeContext {

        private final float alpha;
        private final ColorModel srcColorModel;
        private final ColorModel dstColorModel;

        public AbstractRgbCompositeContext(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
            this.alpha = alpha;
            this.srcColorModel = srcColorModel;
            this.dstColorModel = dstColorModel;
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            float alpha = this.alpha;

            int[] srcPix = null;
            int[] dstPix = null;

            int x = dstOut.getMinX();
            int w = dstOut.getWidth();
            int y0 = dstOut.getMinY();
            int y1 = y0 + dstOut.getHeight();

            for (int y = y0; y < y1; y++) {
                srcPix = src.getPixels(x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                compose(srcPix, dstPix, alpha);
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }

        /**
         * 构成
         *
         * @param src   数据源
         * @param dst   目标源
         * @param alpha alpha
         */
        public abstract void compose(int[] src, int[] dst, float alpha);

        @Override
        public void dispose() {
        }

        // Multiply two numbers in the range 0..255 such that 255*255=255
        static int multiply255(int a, int b) {
            int t = a * b + 0x80;
            return ((t >> 8) + t) >> 8;
        }

        static int clamp(int a) {
            return a < 0 ? 0 : a > 255 ? 255 : a;
        }

    }
}
