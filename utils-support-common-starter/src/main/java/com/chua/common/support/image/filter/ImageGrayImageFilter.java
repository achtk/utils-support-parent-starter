package com.chua.common.support.image.filter;

import java.awt.image.BufferedImage;

/**
 * 灰度化
 *
 * @author CH
 */
public class ImageGrayImageFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        try {
            //创建一个灰度模式的图片
            BufferedImage back = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            int width = src.getWidth();
            int height = src.getHeight();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    back.setRGB(i, j, src.getRGB(i, j));
                }
            }
            return back;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
