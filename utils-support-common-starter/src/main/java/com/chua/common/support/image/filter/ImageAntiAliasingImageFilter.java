package com.chua.common.support.image.filter;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.IoUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 抗锯齿
 *
 * @author CH
 */
@Spi("AntiAliasing")
@SpiOption("抗锯齿滤镜")
public class ImageAntiAliasingImageFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        BufferedImage distImage = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Graphics2D graphics2d = null;
        try {
            graphics2d = distImage.createGraphics();
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2d.drawImage(src.getScaledInstance(src.getWidth(), src.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
        } finally {
            IoUtils.closeQuietly(graphics2d);

        }
        return distImage;
    }
}
