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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.security.SecureRandom;

/**
 * 一种产生模拟拉丝金属图像的过滤器。
 *
 * @author CH
 */
@EqualsAndHashCode()
@Data
@Accessors(chain = true)
public class ImageBrushedMetalFilter implements BufferedImageOp {

    private int radius = 10;
    private float amount = 0.1f;
    private int color = 0xff888888;
    private float shine = 0.1f;
    private boolean monochrome = true;
    private SecureRandom randomNumbers;

    /**
     * Constructs a BrushedMetalFilter object.
     */
    public ImageBrushedMetalFilter() {
    }

    /**
     * Constructs a BrushedMetalFilter object.
     *
     * @param color      an int specifying the metal color
     * @param radius     an int specifying the blur size
     * @param amount     a float specifying the amount of texture
     * @param monochrome a boolean -- true for monochrome texture
     * @param shine      a float specifying the shine to add
     */
    public ImageBrushedMetalFilter(int color, int radius, float amount, boolean monochrome, float shine) {
        this.color = color;
        this.radius = radius;
        this.amount = amount;
        this.monochrome = monochrome;
        this.shine = shine;
    }

    public void blur(int[] in, int[] out, int width, int radius) {
        int widthMinus1 = width - 1;
        int r2 = 2 * radius + 1;
        int tr = 0, tg = 0, tb = 0;

        for (int i = -radius; i <= radius; i++) {
            int rgb = in[mod(i, width)];
            tr += (rgb >> 16) & 0xff;
            tg += (rgb >> 8) & 0xff;
            tb += rgb & 0xff;
        }

        for (int x = 0; x < width; x++) {
            out[x] = 0xff000000 | ((tr / r2) << 16) | ((tg / r2) << 8) | (tb / r2);

            int i1 = x + radius + 1;
            if (i1 > widthMinus1) {
                i1 = mod(i1, width);
            }
            int i2 = x - radius;
            if (i2 < 0) {
                i2 = mod(i2, width);
            }
            int rgb1 = in[i1];
            int rgb2 = in[i2];

            tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
            tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
            tb += (rgb1 & 0xff) - (rgb2 & 0xff);
        }
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel colorModel) {
        if (colorModel == null) {
            colorModel = src.getColorModel();
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), colorModel.isAlphaPremultiplied(), null);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int[] inPixels = new int[width];
        int[] outPixels = new int[width];

        randomNumbers = new SecureRandom();
        int a = color & 0xff000000;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tr = r;
                int tg = g;
                int tb = b;
                if (shine != 0) {
                    int f = (int) (255 * shine * Math.sin((double) x / width * Math.PI));
                    tr += f;
                    tg += f;
                    tb += f;
                }
                if (monochrome) {
                    int n = (int) (255 * (2 * randomNumbers.nextFloat() - 1) * amount);
                    inPixels[x] = a | (clamp(tr + n) << 16) | (clamp(tg + n) << 8) | clamp(tb + n);
                } else {
                    inPixels[x] = a | (random(tr) << 16) | (random(tg) << 8) | random(tb);
                }
            }

            if (radius != 0) {
                blur(inPixels, outPixels, width, radius);
                setRgb(dst, 0, y, width, 1, outPixels);
            } else {
                setRgb(dst, 0, y, width, 1, inPixels);
            }
        }
        return dst;
    }


    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }


    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }


    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }


    private static int clamp(int c) {
        if (c < 0) {
            return 0;
        }
        return Math.min(c, 255);
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     *
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    private static int mod(int a, int b) {
        int n = a / b;

        a -= n * b;
        if (a < 0) {
            return a + b;
        }
        return a;
    }

    private int random(int x) {
        int ff = 0xff;
        x += (int) (255 * (2 * randomNumbers.nextFloat() - 1) * amount);
        if (x < 0) {
            x = 0;
        } else if (x > ff) {
            x = ff;
        }
        return x;
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    private void setRgb(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
}
