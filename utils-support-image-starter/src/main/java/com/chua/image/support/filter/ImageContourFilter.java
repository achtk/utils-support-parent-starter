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


import com.chua.image.support.utils.PixelUtils;

import java.awt.*;

/**
 * 在给定的亮度级别在图像上绘制轮廓的过滤器。
 *
 * @author Administrator
 */
public class ImageContourFilter extends ImageWholeImageFilter {

    private float levels = 5;
    private float scale = 1;
    private float offset = 0;
    private int contourColor = 0xff000000;

    public ImageContourFilter() {
    }

    public int getContourColor() {
        return contourColor;
    }

    public void setContourColor(int contourColor) {
        this.contourColor = contourColor;
    }

    public float getLevels() {
        return levels;
    }

    public void setLevels(float levels) {
        this.levels = levels;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    /**
     * Returns the scale of the contours.
     *
     * @return the scale of the contours.
     * @see #setScale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Specifies the scale of the contours.
     *
     * @param scale the scale of the contours.
     * @min-value 0
     * @max-value 1
     * @see #getScale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        short[][] r = new short[3][width];
        int[] outPixels = new int[width * height];

        int max = 256;
        short[] table = new short[256];
        int offsetl = (int) (offset * 256 / levels);
        for (int i = 0; i < max; i++) {
            table[i] = (short) PixelUtils.clamp((int) (255 * Math.floor(levels * (i + offsetl) / 256) / (levels - 1) - offsetl));
        }

        for (int x = 0; x < width; x++) {
            int rgb = inPixels[x];
            r[1][x] = (short) PixelUtils.brightness(rgb);
        }
        for (int y = 0; y < height; y++) {
            boolean yIn = y > 0 && y < height - 1;
            int nextRowIndex = index + width;
            if (y < height - 1) {
                for (int x = 0; x < width; x++) {
                    int rgb = inPixels[nextRowIndex++];
                    r[2][x] = (short) PixelUtils.brightness(rgb);
                }
            }
            for (int x = 0; x < width; x++) {
                boolean xIn = x > 0 && x < width - 1;
                int w = x - 1;
                int e = x + 1;
                int v = 0;

                if (yIn && xIn) {
                    short nwb = r[0][w];
                    short neb = r[0][x];
                    short swb = r[1][w];
                    short seb = r[1][x];
                    short nw = table[nwb];
                    short ne = table[neb];
                    short sw = table[swb];
                    short se = table[seb];

                    if (nw != ne || nw != sw || ne != se || sw != se) {
                        v = (int) (scale * (Math.abs(nwb - neb) + Math.abs(nwb - swb) + Math.abs(neb - seb) + Math.abs(swb - seb)));
                        if (v > 255) {
                            v = 255;
                        }
                    }
                }

                if (v != 0) {
                    outPixels[index] = PixelUtils.combinePixels(inPixels[index], contourColor, PixelUtils.NORMAL, v);
                } else {
                    outPixels[index] = inPixels[index];
                }
                index++;
            }
            short[] t;
            t = r[0];
            r[0] = r[1];
            r[1] = r[2];
            r[2] = t;
        }

        return outPixels;
    }

}

