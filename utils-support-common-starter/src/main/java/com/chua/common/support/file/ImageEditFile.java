package com.chua.common.support.file;


import com.chua.common.support.image.filter.ImageFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片编辑文件
 *
 * @author CH
 */
public interface ImageEditFile {
    /**
     * 元数据
     *
     * @return 元数据
     */
    ExifFile getExifFile();

    /**
     * 大小
     *
     * @param width  宽度
     * @param height 高度
     * @return this
     */
    ImageEditFile size(int width, int height);

    /**
     * 固定比例
     *
     * @param keepAspectRatio 固定比例
     * @return this
     */
    ImageEditFile keepAspectRatio(boolean keepAspectRatio);

    /**
     * 图片质量
     *
     * @param outputQuality 图片质量
     * @return this
     */
    ImageEditFile outputQuality(float outputQuality);

    /**
     * 使用原始格式
     *
     * @return this
     */
    ImageEditFile useOriginalFormat();

    /**
     * 缩放
     *
     * @param scale 值
     * @return this
     */
    ImageEditFile scale(final float scale);

    /**
     * 旋转
     *
     * @param angle 角度
     * @return this
     */
    ImageEditFile rotate(final double angle);

    /**
     * 裁剪
     *
     * @param rectangle 区域
     * @return this
     */
    ImageEditFile sourceRegion(final Rectangle rectangle);


    /**
     * 添加过滤器
     *
     * @param imageFilter 过滤器
     * @return this
     */
    ImageEditFile addFilter(final ImageFilter imageFilter);

    /**
     * 水印
     *
     * @param image 水印
     * @return this
     */
    ImageEditFile watermark(BufferedImage image);

    /**
     * 水印
     *
     * @param point   位置
     * @param image   水印
     * @param opacity 透明度
     * @return this
     */
    ImageEditFile watermark(Point point, BufferedImage image, float opacity);

    /**
     * 水印
     *
     * @param point 位置
     * @param image 水印
     * @return this
     */
    default ImageEditFile watermark(Point point, BufferedImage image) {
        return watermark(point, image, 1f);
    }
}
