package com.chua.zxing.support.bar.zing;

import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;

/**
 * 二维颜色
 *
 * @author CH
 */
public interface ZingColor {
    /**
     * 渲染
     *
     * @param image     图片
     * @param bitMatrix bitMatrix
     * @return 图片
     */
    BufferedImage render(BufferedImage image, BitMatrix bitMatrix);
}
