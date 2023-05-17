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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * 一种过滤器，可用于通过将目标图像的亮度传输到源的 Alpha 通道来生成擦除。@author CH
 */
public class ImageChromaKeyFilter extends AbstractImageFilter {

    private float toleranceH = 0;
    private float toleranceS = 0;
    private float toleranceB = 0;
    private int color;

    public ImageChromaKeyFilter() {
    }

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

        float[] hsb1 = null;
        float[] hsb2 = null;
        int rgb2 = color;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
        int[] inPixels = null;
        for (int y = 0; y < height; y++) {
            inPixels = getRgb(src, 0, y, width, 1, inPixels);
            for (int x = 0; x < width; x++) {
                int rgb1 = inPixels[x];

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                hsb1 = Color.RGBtoHSB(r1, b1, g1, hsb1);
                if (Math.abs(hsb1[0] - hsb2[0]) < toleranceH && Math.abs(hsb1[1] - hsb2[1]) < toleranceS && Math.abs(hsb1[2] - hsb2[2]) < toleranceB) {
                    inPixels[x] = rgb1 & 0xffffff;
                } else {
                    inPixels[x] = rgb1;
                }
            }
            setRgb(dst, 0, y, width, 1, inPixels);
        }

        return dst;
    }

    public float getToleranceB() {
        return toleranceB;
    }

    public void setToleranceB(float bTolerance) {
        this.toleranceB = bTolerance;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getToleranceH() {
        return toleranceH;
    }

    /**
     * Set the tolerance of the image in the range 0..1.
     * *arg tolerance The tolerance
     */
    public void setToleranceH(float hTolerance) {
        this.toleranceH = hTolerance;
    }

    public float getToleranceS() {
        return toleranceS;
    }

    public void setToleranceS(float sTolerance) {
        this.toleranceS = sTolerance;
    }

    @Override
    public String toString() {
        return "Keying/Chroma Key...";
    }
}
