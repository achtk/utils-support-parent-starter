package com.chua.common.support.image.filter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图像滤镜
 *
 * @author CH
 * @version 1.0.0
 */
public interface ImageFilter {
    /**
     * 获取图片类型
     *
     * @return 图片类型
     */
    String getImageFormat();

    /**
     * 获取图片类型
     *
     * @param name 图片类型
     * @return 图片类型
     */
    String getImageFormat(String name);

    /**
     * 转化
     *
     * @param image 目标
     * @return 结果
     * @throws IOException IOException
     */
    BufferedImage converter(BufferedImage image) throws IOException;

    /**
     * 转化
     *
     * @param image 目标
     * @return 结果
     * @throws IOException IOException
     */
    OutputStream converter(InputStream image) throws IOException;
}
