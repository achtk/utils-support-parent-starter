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

import com.chua.image.support.map.Histogram;
import com.chua.image.support.utils.PixelUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

import static com.chua.common.support.constant.NumberConstant.THIRD;
import static com.chua.image.support.filter.AbstractImagePointFilter.MAX_256;

/**
 * A filter which allows levels adjustment on an image.
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageLevelsFilter extends ImageWholeImageFilter {

    private int[][] lut;
    private float lowLevel = 0;
    private float highLevel = 1;
    private float lowOutputLevel = 0;
    private float highOutputLevel = 1;

    public ImageLevelsFilter() {
    }

    public int filterRgb(int x, int y, int rgb) {
        if (lut != null) {
            int a = rgb & 0xff000000;
            int r = lut[Histogram.RED][(rgb >> 16) & 0xff];
            int g = lut[Histogram.GREEN][(rgb >> 8) & 0xff];
            int b = lut[Histogram.BLUE][rgb & 0xff];

            return a | (r << 16) | (g << 8) | b;
        }
        return rgb;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        Histogram histogram = new Histogram(inPixels, width, height, 0, width);

        int i, j;

        if (histogram.getNumSamples() > 0) {
            float scale = 255.0f / histogram.getNumSamples();
            lut = new int[3][256];

            float low = lowLevel * 255;
            float high = highLevel * 255;
            if (Float.valueOf(low).equals(high)) {
                high++;
            }
            for (i = 0; i < THIRD; i++) {
                for (j = 0; j < MAX_256; j++) {
                    lut[i][j] = PixelUtils.clamp((int) (255 * (lowOutputLevel + (highOutputLevel - lowOutputLevel) * (j - low) / (high - low))));
                }
            }
        } else {
            lut = null;
        }

        i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inPixels[i] = filterRgb(x, y, inPixels[i]);
                i++;
            }
        }
        lut = null;

        return inPixels;
    }

}
