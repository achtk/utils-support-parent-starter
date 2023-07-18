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
import com.chua.image.support.math.Function2D;
import com.chua.image.support.math.ImageFunction2D;
import com.chua.image.support.utils.ImageMath;
import com.chua.image.support.vecmath.Color4f;
import com.chua.image.support.vecmath.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * 阴影滤镜
 *
 * @author Administrator
 */
@Spi("Shad")
@SpiOption("阴影滤镜")
public class ImageShadeFilter extends ImageWholeImageFilter {

    public final static int COLORS_FROM_IMAGE = 0;
    public final static int COLORS_CONSTANT = 1;

    public final static int BUMPS_FROM_IMAGE = 0;
    public final static int BUMPS_FROM_IMAGE_ALPHA = 1;
    public final static int BUMPS_FROM_MAP = 2;
    public final static int BUMPS_FROM_BEVEL = 3;
    protected final static float R_255 = 1.0f / 255.0f;
    private float bumpHeight;
    private float bumpSoftness;
    private final float viewDistance = 10000.0f;
    private final int colorSource = COLORS_FROM_IMAGE;
    private int bumpSource = BUMPS_FROM_IMAGE;
    private Function2D bumpFunction;
    private BufferedImage environmentMap;
    private int[] envPixels;
    private int envWidth = 1, envHeight = 1;
    private final Vector3f l;
    private final Vector3f v;
    private final Vector3f n;
    private final Color4f shadedColor;
    private final Color4f diffuseColor;
    private final Color4f specularColor;
    private final Vector3f tmpV;
    private final Vector3f tmpV2;

    public ImageShadeFilter() {
        bumpHeight = 1.0f;
        bumpSoftness = 5.0f;
        l = new Vector3f();
        v = new Vector3f();
        n = new Vector3f();
        shadedColor = new Color4f();
        diffuseColor = new Color4f();
        specularColor = new Color4f();
        tmpV = new Vector3f();
        tmpV2 = new Vector3f();
    }

    public Function2D getBumpFunction() {
        return bumpFunction;
    }

    public void setBumpFunction(Function2D bumpFunction) {
        this.bumpFunction = bumpFunction;
    }

    public float getBumpHeight() {
        return bumpHeight;
    }

    public void setBumpHeight(float bumpHeight) {
        this.bumpHeight = bumpHeight;
    }

    public float getBumpSoftness() {
        return bumpSoftness;
    }

    public void setBumpSoftness(float bumpSoftness) {
        this.bumpSoftness = bumpSoftness;
    }

    public int getBumpSource() {
        return bumpSource;
    }

    public void setBumpSource(int bumpSource) {
        this.bumpSource = bumpSource;
    }

    public BufferedImage getEnvironmentMap() {
        return environmentMap;
    }

    public void setEnvironmentMap(BufferedImage environmentMap) {
        this.environmentMap = environmentMap;
        if (environmentMap != null) {
            envWidth = environmentMap.getWidth();
            envHeight = environmentMap.getHeight();
            envPixels = getRgb(environmentMap, 0, 0, envWidth, envHeight, null);
        } else {
            envWidth = envHeight = 1;
            envPixels = null;
        }
    }

    protected void setFromRgb(Color4f c, int argb) {
        c.set(((argb >> 16) & 0xff) * R_255, ((argb >> 8) & 0xff) * R_255, (argb & 0xff) * R_255, ((argb >> 24) & 0xff) * R_255);
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] outPixels = new int[width * height];
        float width45 = Math.abs(6.0f * bumpHeight);
        boolean invertBumps = bumpHeight < 0;
        Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f viewpoint = new Vector3f((float) width / 2.0f, (float) height / 2.0f, viewDistance);
        Vector3f normal = new Vector3f();
        Color4f c = new Color4f();
        Function2D bump = bumpFunction;

        if (bumpSource == BUMPS_FROM_IMAGE || bumpSource == BUMPS_FROM_IMAGE_ALPHA || bumpSource == BUMPS_FROM_MAP || bump == null) {
            if (bumpSoftness != 0) {
                int bumpWidth = width;
                int bumpHeight = height;
                int[] bumpPixels = inPixels;
                if (bumpSource == BUMPS_FROM_MAP && bumpFunction instanceof ImageFunction2D) {
                    ImageFunction2D if2d = (ImageFunction2D) bumpFunction;
                    bumpWidth = if2d.getWidth();
                    bumpHeight = if2d.getHeight();
                    bumpPixels = if2d.getPixels();
                }
                Kernel kernel = ImageGaussianFilter.makeKernel(bumpSoftness);
                int[] tmpPixels = new int[bumpWidth * bumpHeight];
                int[] softPixels = new int[bumpWidth * bumpHeight];
                ImageGaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, false, false, ImageConvolveFilter.CLAMP_EDGES);
                ImageGaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, false, false, ImageConvolveFilter.CLAMP_EDGES);
                bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
            } else {
                bump = new ImageFunction2D(inPixels, width, height, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
            }
        }

        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f n = new Vector3f();

        // Loop through each source pixel
        for (int y = 0; y < height; y++) {
            float ny = y;
            position.y = y;
            for (int x = 0; x < width; x++) {
                float nx = x;

                // Calculate the normal at this point
                if (bumpSource != BUMPS_FROM_BEVEL) {
                    // Complicated and slower method
                    // Calculate four normals using the gradients in +/- X/Y directions
                    int count = 0;
                    normal.x = normal.y = normal.z = 0;
                    float m0 = width45 * bump.evaluate(nx, ny);
                    float m1 = x > 0 ? width45 * bump.evaluate(nx - 1.0f, ny) - m0 : -2;
                    float m2 = y > 0 ? width45 * bump.evaluate(nx, ny - 1.0f) - m0 : -2;
                    float m3 = x < width - 1 ? width45 * bump.evaluate(nx + 1.0f, ny) - m0 : -2;
                    float m4 = y < height - 1 ? width45 * bump.evaluate(nx, ny + 1.0f) - m0 : -2;

                    if (m1 != -2 && m4 != -2) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m1;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m4;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        count++;
                    }

                    if (m1 != -2 && m2 != -2) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m1;
                        v2.x = 0.0f;
                        v2.y = -1.0f;
                        v2.z = m2;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        count++;
                    }

                    if (m2 != -2 && m3 != -2) {
                        v1.x = 0.0f;
                        v1.y = -1.0f;
                        v1.z = m2;
                        v2.x = 1.0f;
                        v2.y = 0.0f;
                        v2.z = m3;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        count++;
                    }

                    if (m3 != -2 && m4 != -2) {
                        v1.x = 1.0f;
                        v1.y = 0.0f;
                        v1.z = m3;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m4;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        count++;
                    }

                    // Average the four normals
                    normal.x /= count;
                    normal.y /= count;
                    normal.z /= count;
                }


                if (invertBumps) {
                    normal.x = -normal.x;
                    normal.y = -normal.y;
                }
                position.x = x;

                if (normal.z >= 0) {
                    // Get the material colour at this point
                    if (environmentMap != null) {
                        tmpV2.set(viewpoint);
                        tmpV2.sub(position);
                        tmpV2.normalize();
                        tmpV.set(normal);
                        tmpV.normalize();

                        // Reflect
                        tmpV.scale(2.0f * tmpV.dot(tmpV2));
                        tmpV.sub(v);

                        tmpV.normalize();
                        setFromRgb(c, getEnvironmentMapP(normal, inPixels, width, height));
                        int alpha = inPixels[index] & 0xff000000;
                        int rgb = ((int) (c.x * 255) << 16) | ((int) (c.y * 255) << 8) | (int) (c.z * 255);
                        outPixels[index++] = alpha | rgb;
                    } else {
                        outPixels[index++] = 0;
                    }
                } else {
                    outPixels[index++] = 0;
                }
            }
        }
        return outPixels;
    }

    private int getEnvironmentMapP(Vector3f normal, int[] inPixels, int width, int height) {
        if (environmentMap != null) {
            float x = 0.5f * (1 + normal.x);
            float y = 0.5f * (1 + normal.y);
            x = ImageMath.clamp(x * envWidth, 0, envWidth - 1);
            y = ImageMath.clamp(y * envHeight, 0, envHeight - 1);
            int ix = (int) x;
            int iy = (int) y;

            float xWeight = x - ix;
            float yWeight = y - iy;
            int i = envWidth * iy + ix;
            int dx = ix == envWidth - 1 ? 0 : 1;
            int dy = iy == envHeight - 1 ? 0 : envWidth;
            return ImageMath.bilinearInterpolate(xWeight, yWeight, envPixels[i], envPixels[i + dx], envPixels[i + dy], envPixels[i + dx + dy]);
        }
        return 0;
    }

}
