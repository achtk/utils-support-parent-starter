/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.chua.image.support.filter;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * 使用提供的 Paint 在图像周围添加边框的过滤器，如果没有绘画，它可能为 null。
 *
 * @author CH
 */
@Spi("Border")
@SpiOption("边框滤镜")
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ImageBorderFilter extends AbstractImageFilter {

    private int leftBorder, rightBorder;
    private int topBorder, bottomBorder;
    private Paint borderPaint;

    /**
     * Construct a BorderFilter which does nothing.
     */
    public ImageBorderFilter() {
    }

    /**
     * Construct a BorderFilter.
     *
     * @param leftBorder   the left border value
     * @param topBorder    the top border value
     * @param rightBorder  the right border value
     * @param bottomBorder the bottom border value
     * @param borderPaint  the paint with which to fill the border
     */
    public ImageBorderFilter(int leftBorder, int topBorder, int rightBorder, int bottomBorder, Paint borderPaint) {
        this.leftBorder = leftBorder;
        this.topBorder = topBorder;
        this.rightBorder = rightBorder;
        this.bottomBorder = bottomBorder;
        this.borderPaint = borderPaint;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = new BufferedImage(width + leftBorder + rightBorder, height + topBorder + bottomBorder, src.getType());
        }
        Graphics2D g = dst.createGraphics();
        if (borderPaint != null) {
            g.setPaint(borderPaint);
            if (leftBorder > 0) {
                g.fillRect(0, 0, leftBorder, height);
            }
            if (rightBorder > 0) {
                g.fillRect(width - rightBorder, 0, rightBorder, height);
            }
            if (topBorder > 0) {
                g.fillRect(leftBorder, 0, width - leftBorder - rightBorder, topBorder);
            }
            if (bottomBorder > 0) {
                g.fillRect(leftBorder, height - bottomBorder, width - leftBorder - rightBorder, bottomBorder);
            }
        }
        g.drawRenderedImage(src, AffineTransform.getTranslateInstance(leftBorder, rightBorder));
        g.dispose();
        return dst;
    }

}
