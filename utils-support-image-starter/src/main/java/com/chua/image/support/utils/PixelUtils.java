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

package com.chua.image.support.utils;

import java.awt.*;
import java.security.SecureRandom;

/**
 * 一些更有用的图像处理数学函数。
 * 随着我们转向 Java2D，这些已经过时了。请改用 MiscComposite。
 *
 * @author CH
 */
public class PixelUtils {

    public final static int REPLACE = 0;
    public final static int NORMAL = 1;
    public final static int MIN = 2;
    public final static int MAX = 3;
    public final static int ADD = 4;
    public final static int SUBTRACT = 5;
    public final static int DIFFERENCE = 6;
    public final static int MULTIPLY = 7;
    public final static int HUE = 8;
    public final static int SATURATION = 9;
    public final static int VALUE = 10;
    public final static int COLOR = 11;
    public final static int SCREEN = 12;
    public final static int AVERAGE = 13;
    public final static int OVERLAY = 14;
    public final static int CLEAR = 15;
    public final static int EXCHANGE = 16;
    public final static int DISSOLVE = 17;
    public final static int DST_IN = 18;
    public final static int ALPHA = 19;
    public final static int ALPHA_TO_GRAY = 20;
    private final static float[] HSB1 = new float[3];
    private final static float[] HSB2 = new float[3];
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static int brightness(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }

    /**
     * 将值限制在 0..255 范围内
     */
    public static int clamp(int c) {
        if (c < 0) {
            return 0;
        }
        return Math.min(c, 255);
    }

    /**
     * 返回绘制到 rgb2 上的 rgb1
     *
     * @param rgb1 参数
     * @param rgb2 参数
     * @param op   参数
     * @return 结果
     */
    public static int combinePixels(int rgb1, int rgb2, int op) {
        return combinePixels(rgb1, rgb2, op, 0xff);
    }

    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha, int channelMask) {
        return (rgb2 & ~channelMask) | combinePixels(rgb1 & channelMask, rgb2, op, extraAlpha);
    }

    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha) {
        if (op == REPLACE) {
            return rgb1;
        }
        int a1 = (rgb1 >> 24) & 0xff, r1 = (rgb1 >> 16) & 0xff, g1 = (rgb1 >> 8) & 0xff, b1 = rgb1 & 0xff, a2 = (rgb2 >> 24) & 0xff, r2 = (rgb2 >> 16) & 0xff, g2 = (rgb2 >> 8) & 0xff, b2 = rgb2 & 0xff, ff = 0xff;
        switch (op) {
            case NORMAL:
                break;
            case MIN:
                r1 = Math.min(r1, r2);
                g1 = Math.min(g1, g2);
                b1 = Math.min(b1, b2);
                break;
            case MAX:
                r1 = Math.max(r1, r2);
                g1 = Math.max(g1, g2);
                b1 = Math.max(b1, b2);
                break;
            case ADD:
                r1 = clamp(r1 + r2);
                g1 = clamp(g1 + g2);
                b1 = clamp(b1 + b2);
                break;
            case SUBTRACT:
                r1 = clamp(r2 - r1);
                g1 = clamp(g2 - g1);
                b1 = clamp(b2 - b1);
                break;
            case DIFFERENCE:
                r1 = clamp(Math.abs(r1 - r2));
                g1 = clamp(Math.abs(g1 - g2));
                b1 = clamp(Math.abs(b1 - b2));
                break;
            case MULTIPLY:
                r1 = clamp(r1 * r2 / 255);
                g1 = clamp(g1 * g2 / 255);
                b1 = clamp(b1 * b2 / 255);
                break;
            case DISSOLVE:
                if ((SECURE_RANDOM.nextInt() & ff) <= a1) {
                    r1 = r2;
                    g1 = g2;
                    b1 = b2;
                }
                break;
            case AVERAGE:
                r1 = (r1 + r2) / 2;
                g1 = (g1 + g2) / 2;
                b1 = (b1 + b2) / 2;
                break;
            case HUE:
            case SATURATION:
            case VALUE:
            case COLOR:
                Color.RGBtoHSB(r1, g1, b1, HSB1);
                Color.RGBtoHSB(r2, g2, b2, HSB2);
                switch (op) {
                    case HUE:
                        HSB2[0] = HSB1[0];
                        break;
                    case SATURATION:
                        HSB2[1] = HSB1[1];
                        break;
                    case VALUE:
                        HSB2[2] = HSB1[2];
                        break;
                    case COLOR:
                        HSB2[0] = HSB1[0];
                        HSB2[1] = HSB1[1];
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + op);
                }
                rgb1 = Color.HSBtoRGB(HSB2[0], HSB2[1], HSB2[2]);
                r1 = (rgb1 >> 16) & 0xff;
                g1 = (rgb1 >> 8) & 0xff;
                b1 = rgb1 & 0xff;
                break;
            case SCREEN:
                r1 = 255 - ((255 - r1) * (255 - r2)) / 255;
                g1 = 255 - ((255 - g1) * (255 - g2)) / 255;
                b1 = 255 - ((255 - b1) * (255 - b2)) / 255;
                break;
            case OVERLAY:
                int m, s;
                s = 255 - ((255 - r1) * (255 - r2)) / 255;
                m = r1 * r2 / 255;
                r1 = (s * r1 + m * (255 - r1)) / 255;
                s = 255 - ((255 - g1) * (255 - g2)) / 255;
                m = g1 * g2 / 255;
                g1 = (s * g1 + m * (255 - g1)) / 255;
                s = 255 - ((255 - b1) * (255 - b2)) / 255;
                m = b1 * b2 / 255;
                b1 = (s * b1 + m * (255 - b1)) / 255;
                break;
            case CLEAR:
                r1 = g1 = b1 = 0xff;
                break;
            case DST_IN:
                r1 = clamp((r2 * a1) / 255);
                g1 = clamp((g2 * a1) / 255);
                b1 = clamp((b2 * a1) / 255);
                a1 = clamp((a2 * a1) / 255);
                return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
            case ALPHA:
                a1 = a1 * a2 / 255;
                return (a1 << 24) | (r2 << 16) | (g2 << 8) | b2;
            case ALPHA_TO_GRAY:
                int na = 255 - a1;
                return (a1 << 24) | (na << 16) | (na << 8) | na;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
        if (extraAlpha != ff || a1 != ff) {
            a1 = a1 * extraAlpha / 255;
            int a3 = (255 - a1) * a2 / 255;
            r1 = clamp((r1 * a1 + r2 * a3) / 255);
            g1 = clamp((g1 * a1 + g2 * a3) / 255);
            b1 = clamp((b1 * a1 + b2 * a3) / 255);
            a1 = clamp(a1 + a3);
        }
        return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
    }

    public static int interpolate(int v1, int v2, float f) {
        return clamp((int) (v1 + f * (v2 - v1)));
    }

    public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) <= tolerance && Math.abs(g1 - g2) <= tolerance && Math.abs(b1 - b2) <= tolerance;
    }

}
