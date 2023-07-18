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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * 平铺滤镜
 *
 * @author Administrator
 */
@Spi("TileImage")
@SpiOption("平铺滤镜")
public class ImageTileImageFilter extends AbstractImageFilter {

    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    /**
     * Construct a TileImageFilter.
     */
    public ImageTileImageFilter() {
        this(-1, -1);
    }

    /**
     * Construct a TileImageFilter.
     *
     * @param width  the output image width
     * @param height the output image height
     */
    public ImageTileImageFilter(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int tileWidth = src.getWidth();
        int tileHeight = src.getHeight();

        if (height == -1) {
            height = tileHeight;
        }

        if (width == -1) {
            width = tileHeight;
        }

        if (dst == null) {
            ColorModel colorModel = src.getColorModel();
            dst = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width, height), colorModel.isAlphaPremultiplied(), null);
        }


        Graphics2D g = dst.createGraphics();
        for (int y = 0; y < height; y += tileHeight) {
            for (int x = 0; x < width; x += tileWidth) {
                g.drawImage(src, null, x, y);
            }
        }
        g.dispose();

        return dst;
    }

    /**
     * Get the output image height.
     *
     * @return the height
     * @see #setHeight
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the output image height.
     *
     * @param height the height
     * @see #getHeight
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the output image width.
     *
     * @return the width
     * @see #setWidth
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the output image width.
     *
     * @param width the width
     * @see #getWidth
     */
    public void setWidth(int width) {
        this.width = width;
    }

}
