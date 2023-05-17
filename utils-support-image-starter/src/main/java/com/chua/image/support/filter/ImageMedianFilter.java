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

import static com.chua.common.support.constant.NumberConstant.FOUR;
import static com.chua.common.support.constant.NumberConstant.NIGHT;

/**
 * A filter which performs a 3x3 median operation. Useful for removing dust and noise.
 *
 * @author Administrator
 */
public class ImageMedianFilter extends ImageWholeImageFilter {

    public ImageMedianFilter() {
    }

    private int median(int[] array) {
        int max, maxIndex;

        for (int i = 0; i < FOUR; i++) {
            max = 0;
            maxIndex = 0;
            for (int j = 0; j < NIGHT; j++) {
                if (array[j] > max) {
                    max = array[j];
                    maxIndex = j;
                }
            }
            array[maxIndex] = 0;
        }
        max = 0;
        for (int i = 0; i < NIGHT; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private int rgbMedian(int[] r, int[] g, int[] b) {
        int sum, index = 0, min = Integer.MAX_VALUE;

        for (int i = 0; i < NIGHT; i++) {
            sum = 0;
            for (int j = 0; j < NIGHT; j++) {
                sum += Math.abs(r[i] - r[j]);
                sum += Math.abs(g[i] - g[j]);
                sum += Math.abs(b[i] - b[j]);
            }
            if (sum < min) {
                min = sum;
                index = i;
            }
        }
        return index;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] argb = new int[9];
        int[] r = new int[9];
        int[] g = new int[9];
        int[] b = new int[9];
        int[] outPixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int k = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    int iy = y + dy;
                    if (0 <= iy && iy < height) {
                        int ioffset = iy * width;
                        for (int dx = -1; dx <= 1; dx++) {
                            int ix = x + dx;
                            if (0 <= ix && ix < width) {
                                int rgb = inPixels[ioffset + ix];
                                argb[k] = rgb;
                                r[k] = (rgb >> 16) & 0xff;
                                g[k] = (rgb >> 8) & 0xff;
                                b[k] = rgb & 0xff;
                                k++;
                            }
                        }
                    }
                }
                while (k < NIGHT) {
                    argb[k] = 0xff000000;
                    r[k] = g[k] = b[k] = 0;
                    k++;
                }
                outPixels[index++] = argb[rgbMedian(r, g, b)];
            }
        }
        return outPixels;
    }

}

