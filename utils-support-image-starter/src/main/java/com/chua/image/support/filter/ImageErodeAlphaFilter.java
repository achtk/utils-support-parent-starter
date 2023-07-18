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
import com.chua.image.support.utils.ImageMath;

import java.awt.image.BufferedImage;

/**
 * 图像侵蚀 Alpha 过滤器
 *
 * @author Administrator
 */
@Spi("ErodeAlpha")
@SpiOption("图像侵蚀滤镜")
public class ImageErodeAlphaFilter extends AbstractImagePointFilter {

    protected float radius = 5;
    private float threshold;
    private float softness = 0;
    private float lowerThreshold;
    private float upperThreshold;

    public ImageErodeAlphaFilter() {
        this(3, 0.75f, 0);
    }

    public ImageErodeAlphaFilter(float radius, float threshold, float softness) {
        this.radius = radius;
        this.threshold = threshold;
        this.softness = softness;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        dst = new ImageGaussianFilter((int) radius).filter(src, null);
        lowerThreshold = 255 * (threshold - softness * 0.5f);
        upperThreshold = 255 * (threshold + softness * 0.5f);
        return super.filter(dst, dst);
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        int a = (rgb >> 24) & 0xff;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        int max = 255;
        if (a == max) {
            return 0xffffffff;
        }
        float f = ImageMath.smoothStep(lowerThreshold, upperThreshold, (float) a);
        a = (int) (f * 255);
        if (a < 0) {
            a = 0;
        } else if (a > max) {
            a = 255;
        }
        return (a << 24) | 0xffffff;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getSoftness() {
        return softness;
    }

    public void setSoftness(float softness) {
        this.softness = softness;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
}
