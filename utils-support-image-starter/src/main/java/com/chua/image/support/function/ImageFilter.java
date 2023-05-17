package com.chua.image.support.function;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 图像滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public interface ImageFilter extends com.chua.common.support.image.filter.ImageFilter, net.coobird.thumbnailator.filters.ImageFilter {
    /**
     * 转化
     *
     * @param image 目标
     * @return 结果
     * @throws IOException IOException
     */
    @Override
    BufferedImage converter(BufferedImage image) throws IOException;

    /**
     * 转化
     *
     * @param img 目标
     * @return 结果
     */
    @Override
    default BufferedImage apply(BufferedImage img) {
        try {
            return converter(img);
        } catch (IOException e) {
            return img;
        }
    }
}
