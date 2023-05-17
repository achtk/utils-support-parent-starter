package com.chua.image.support.filter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 图像滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public abstract class AbstractImageFilter extends com.chua.common.support.image.filter.AbstractImageFilter implements net.coobird.thumbnailator.filters.ImageFilter {

    protected SecureRandom randomNumbers = new SecureRandom();

    @Override
    public BufferedImage converter(BufferedImage image) throws IOException {
        return filter(image, null);
    }

    /**
     * 创建兼容的目标图像
     *
     * @param src        元数据
     * @param colorModel 颜色模型
     * @return 图像
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel colorModel) {
        if (colorModel == null) {
            colorModel = src.getColorModel();
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), colorModel.isAlphaPremultiplied(), null);
    }

    /**
     * 滤镜
     *
     * @param src 目标
     * @param dst 源
     * @return 结果
     */
    abstract public BufferedImage filter(BufferedImage src, BufferedImage dst);

    /**
     * 获取获得边界 2 D
     *
     * @param src 元数据
     * @return 边界 2 D
     */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    /**
     * 获得点 2 D
     *
     * @param srcPt 原始点
     * @param dstPt 目标点
     * @return 结果
     */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    /**
     * 从图像中获取 ARGB 像素的便捷方法。这试图避免 BufferedImage.getRGB 取消管理图像的性能损失.
     *
     * @param image  一个 BufferedImage 对象
     * @param x      像素块的左边缘
     * @param y      像素块的右边缘
     * @param width  像素阵列的宽度
     * @param height 像素阵列的高度
     * @param pixels 保存返回像素的数组。可能为空。
     * @return 像素
     * @see #setRgb
     */
    public int[] getRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    /**
     * 一种在图像中设置 ARGB 像素的便捷方法。这试图避免 BufferedImage.setRGB 取消管理图像的性能*损失。
     *
     * @param image  一个 BufferedImage 对象
     * @param x      像素块的左边缘
     * @param y      像素块的右边缘
     * @param width  像素阵列的宽度
     * @param height 像素阵列的高度
     * @param pixels 保存返回像素的数组。可能为空。
     * @see #getRgb
     */
    public void setRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }

    @Override
    public BufferedImage apply(BufferedImage img) {
        return filter(img, null);
    }
}
