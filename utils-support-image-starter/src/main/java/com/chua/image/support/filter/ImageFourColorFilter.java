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

/**
 * A filter which draws a gradient interpolated between four colors defined at the corners of the image.
 *
 * @author Administrator
 */
@Spi("FourColor")
@SpiOption("渐变插值滤镜")
public class ImageFourColorFilter extends AbstractImagePointFilter {

    private int width;
    private int height;
    private int colorNw;
    private int colorNe;
    private int colorSw;
    private int colorSe;
    private int rnw, gnw, bnw;
    private int rne, gne, bne;
    private int rsw, gsw, bsw;
    private int rse, gse, bse;

    public ImageFourColorFilter() {
        setColorNw(0xffff0000);
        setColorNe(0xffff00ff);
        setColorSw(0xff0000ff);
        setColorSe(0xff00ffff);
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        float fx = (float) x / width;
        float fy = (float) y / height;
        float p, q;

        p = rnw + (rne - rnw) * fx;
        q = rsw + (rse - rsw) * fx;
        int r = (int) (p + (q - p) * fy + 0.5f);

        p = gnw + (gne - gnw) * fx;
        q = gsw + (gse - gsw) * fx;
        int g = (int) (p + (q - p) * fy + 0.5f);

        p = bnw + (bne - bnw) * fx;
        q = bsw + (bse - bsw) * fx;
        int b = (int) (p + (q - p) * fy + 0.5f);

        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    public int getColorNe() {
        return colorNe;
    }

    public void setColorNe(int color) {
        this.colorNe = color;
        rne = (color >> 16) & 0xff;
        gne = (color >> 8) & 0xff;
        bne = color & 0xff;
    }

    public int getColorNw() {
        return colorNw;
    }

    public void setColorNw(int color) {
        this.colorNw = color;
        rnw = (color >> 16) & 0xff;
        gnw = (color >> 8) & 0xff;
        bnw = color & 0xff;
    }

    public int getColorSe() {
        return colorSe;
    }

    public void setColorSe(int color) {
        this.colorSe = color;
        rse = (color >> 16) & 0xff;
        gse = (color >> 8) & 0xff;
        bse = color & 0xff;
    }

    public int getColorSw() {
        return colorSw;
    }

    public void setColorSw(int color) {
        this.colorSw = color;
        rsw = (color >> 16) & 0xff;
        gsw = (color >> 8) & 0xff;
        bsw = color & 0xff;
    }

    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        super.setDimensions(width, height);
    }
}
