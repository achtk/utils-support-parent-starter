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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.SecureRandom;

/**
 * A filter which adds random noise into an image.
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageNoiseFilter extends AbstractImagePointFilter {

    /**
     * 噪声的高斯分布。
     */
    public final static int GAUSSIAN = 0;

    /**
     * 噪声的均匀分布。
     */
    public final static int UNIFORM = 1;

    private int amount = 25;
    private int distribution = UNIFORM;
    private boolean monochrome = false;
    private float density = 1;
    private SecureRandom randomNumbers = new SecureRandom();

    public ImageNoiseFilter() {
    }

    public ImageNoiseFilter(boolean monochrome) {
        setMonochrome(monochrome);
    }

    public ImageNoiseFilter(int salt, boolean monochrome) {
        setAmount(salt);
        setMonochrome(monochrome);
    }

    public ImageNoiseFilter(int salt) {
        setAmount(salt);
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        if (randomNumbers.nextFloat() <= density) {
            int a = rgb & 0xff000000;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            if (monochrome) {
                int n = (int) (((distribution == GAUSSIAN ? randomNumbers.nextGaussian() : 2 * randomNumbers.nextFloat() - 1)) * amount);
                r = PixelUtils.clamp(r + n);
                g = PixelUtils.clamp(g + n);
                b = PixelUtils.clamp(b + n);
            } else {
                r = random(r);
                g = random(g);
                b = random(b);
            }
            return a | (r << 16) | (g << 8) | b;
        }
        return rgb;
    }

    private int random(int x) {
        int ff = 0xff;
        x += (int) (((distribution == GAUSSIAN ? randomNumbers.nextGaussian() : 2 * randomNumbers.nextFloat() - 1)) * amount);
        if (x < 0) {
            x = 0;
        } else if (x > ff) {
            x = 0xff;
        }
        return x;
    }

}
