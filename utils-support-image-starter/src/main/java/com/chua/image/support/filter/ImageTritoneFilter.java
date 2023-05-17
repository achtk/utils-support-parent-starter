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

import com.chua.image.support.utils.ImageMath;
import com.chua.image.support.utils.PixelUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * A filter which performs a tritone conversion on an image. Given three colors for shadows, midtones and highlights,
 * it converts the image to grayscale and then applies a color mapping based on the colors.
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageTritoneFilter extends AbstractImagePointFilter {

    private int shadowColor = 0xff000000;
    private int midColor = 0xff888888;
    private int highColor = 0xffffffff;
    private int[] lut;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        lut = new int[256];
        for (int i = 0; i < MAX_128; i++) {
            float t = i / 127.0f;
            lut[i] = ImageMath.mixColors(t, shadowColor, midColor);
        }
        for (int i = 128; i < MAX_256; i++) {
            float t = (i - 127) / 128.0f;
            lut[i] = ImageMath.mixColors(t, midColor, highColor);
        }
        dst = super.filter(src, dst);
        lut = null;
        return dst;
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        return lut[PixelUtils.brightness(rgb)];
    }

}

