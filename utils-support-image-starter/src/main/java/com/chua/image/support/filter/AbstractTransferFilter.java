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

import java.awt.image.BufferedImage;

/**
 * 传输过滤器
 *
 * @author Administrator
 */
public abstract class AbstractTransferFilter extends AbstractImagePointFilter {

    protected int[] rTable, gTable, bTable;
    protected boolean initialized = false;

    public AbstractTransferFilter() {
        canFilterIndexColorModel = true;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!initialized) {
            initialize();
        }
        return super.filter(src, dst);
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        int a = rgb & 0xff000000;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        r = rTable[r];
        g = gTable[g];
        b = bTable[b];
        return a | (r << 16) | (g << 8) | b;
    }

    public int[] getLut() {
        if (!initialized) {
            initialize();
        }
        int max = 256;
        int[] lut = new int[max];
        for (int i = 0; i < max; i++) {
            lut[i] = filterRgb(0, 0, (i << 24) | (i << 16) | (i << 8) | i);
        }
        return lut;
    }

    protected void initialize() {
        initialized = true;
        rTable = gTable = bTable = makeTable();
    }

    protected int[] makeTable() {
        int max = 256;
        int[] table = new int[max];
        for (int i = 0; i < max; i++) {
            table[i] = PixelUtils.clamp((int) (255 * transferFunction(i / 255.0f)));
        }
        return table;
    }

    protected float transferFunction(float v) {
        return 0;
    }

}

