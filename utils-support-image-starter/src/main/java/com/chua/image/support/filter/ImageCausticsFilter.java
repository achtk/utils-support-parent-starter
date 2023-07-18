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
import com.chua.image.support.math.Noise;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.awt.*;
import java.security.SecureRandom;

import static com.chua.common.support.constant.NumberConstant.THIRD;
import static com.chua.common.support.constant.NumberConstant.TWE;

/**
 * 模拟水下焦散的过滤器。这可以被动画化以获得游泳池底部的效果。
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Spi("Caustics")
@SpiOption("水下焦散滤镜")
@Accessors(chain = true)
public class ImageCausticsFilter extends ImageWholeImageFilter {

    private float scale = 32;
    private float angle = 0.0f;
    private int brightness = 10;
    private float amount = 1.0f;
    private float turbulence = 1.0f;
    private float dispersion = 0.0f;
    private float time = 0.0f;
    private int samples = 2;
    private int bgColor = 0xff799fff;

    private float s, c;

    public ImageCausticsFilter() {
    }


    private static int add(int rgb, float brightness) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        r += brightness;
        g += brightness;
        b += brightness;
        int max = 255;
        if (r > max) {
            r = max;
        }
        if (g > max) {
            g = max;
        }
        if (b > max) {
            b = max;
        }
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    private static int add(int rgb, float brightness, int c) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        int max = 255;
        if (c == TWE) {
            r += brightness;
        } else if (c == 1) {
            g += brightness;
        } else {
            b += brightness;
        }
        if (r > max) {
            r = max;
        }
        if (g > max) {
            g = max;
        }
        if (b > max) {
            b = max;
        }
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    private static float turbulence2(float x, float y, float time, float octaves) {
        float value = 0.0f;
        float remainder;
        float lacunarity = 2.0f;
        float f = 1.0f;
        int i;

        // to prevent "cascading" effects
        x += 371;
        y += 529;

        for (i = 0; i < (int) octaves; i++) {
            value += Noise.noise3(x, y, time) / f;
            x *= lacunarity;
            y *= lacunarity;
            f *= 2;
        }

        remainder = octaves - (int) octaves;
        if (remainder != 0) {
            value += remainder * Noise.noise3(x, y, time) / f;
        }

        return value;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        SecureRandom random = new SecureRandom();
        s = (float) Math.sin(0.1);
        c = (float) Math.cos(0.1);
        int outWidth = transformedSpace.width, outHeight = transformedSpace.height, index = 0;
        int[] pixels = new int[outWidth * outHeight];
        for (int y = 0; y < outHeight; y++) {
            for (int x = 0; x < outWidth; x++) {
                pixels[index++] = bgColor;
            }
        }
        int v = brightness / samples;
        if (v == 0) {
            v = 1;
        }
        float rs = 1.0f / scale, d = 0.95f;
        for (int y = 0; y < outHeight; y++) {
            for (int x = 0; x < outWidth; x++) {
                for (int s = 0; s < samples; s++) {
                    float sx = x + random.nextFloat(), sy = y + random.nextFloat(), nx = sx * rs, ny = sy * rs;
                    float xDisplacement, yDisplacement, focus = 0.1f + amount;
                    xDisplacement = evaluate(nx - d, ny) - evaluate(nx + d, ny);
                    yDisplacement = evaluate(nx, ny + d) - evaluate(nx, ny - d);
                    if (dispersion > 0) {
                        for (int c = 0; c < THIRD; c++) {
                            float ca = (1 + c * dispersion);
                            float srcX = sx + scale * focus * xDisplacement * ca;
                            float srcY = sy + scale * focus * yDisplacement * ca;
                            if (srcX < 0 || srcX >= outWidth - 1 || srcY < 0 || srcY >= outHeight - 1) {
                            } else {
                                int i = ((int) srcY) * outWidth + (int) srcX;
                                int rgb = pixels[i];
                                int r = (rgb >> 16) & 0xff;
                                int g = (rgb >> 8) & 0xff;
                                int b = rgb & 0xff;
                                if (c == 2) {
                                    r += v;
                                } else if (c == 1) {
                                    g += v;
                                } else {
                                    b += v;
                                }
                                if (r > 255) {
                                    r = 255;
                                }
                                if (g > 255) {
                                    g = 255;
                                }
                                if (b > 255) {
                                    b = 255;
                                }
                                pixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
                            }
                        }
                    } else {
                        float srcX = sx + scale * focus * xDisplacement;
                        float srcY = sy + scale * focus * yDisplacement;
                        if (srcX < 0 || srcX >= outWidth - 1 || srcY < 0 || srcY >= outHeight - 1) {
                        } else {
                            int i = ((int) srcY) * outWidth + (int) srcX;
                            int rgb = pixels[i];
                            int r = (rgb >> 16) & 0xff;
                            int g = (rgb >> 8) & 0xff;
                            int b = rgb & 0xff;
                            r += v;
                            g += v;
                            b += v;
                            if (r > 255) {
                                r = 255;
                            }
                            if (g > 255) {
                                g = 255;
                            }
                            if (b > 255) {
                                b = 255;
                            }
                            pixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
                        }
                    }
                }
            }
        }
        return pixels;
    }

    private float evaluate(float x, float y) {
        float xt = s * x + c * time;
        float tt = c * x - c * time;
        float f = turbulence == 0.0 ? Noise.noise3(xt, y, tt) : turbulence2(xt, y, tt, turbulence);
        return f;
    }

}
