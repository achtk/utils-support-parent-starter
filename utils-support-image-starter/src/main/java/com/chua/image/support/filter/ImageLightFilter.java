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


import com.chua.image.support.math.Function2D;
import com.chua.image.support.math.ImageFunction2D;
import com.chua.image.support.utils.ImageMath;
import com.chua.image.support.vecmath.Color4f;
import com.chua.image.support.vecmath.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.util.Vector;

import static com.chua.common.support.constant.NumberConstant.*;

/**
 * A filter which produces lighting and embossing effects.
 *
 * @author Administrator
 */
public class ImageLightFilter extends ImageWholeImageFilter {

    /**
     * Take the output colors from the input image.
     */
    public final static int COLORS_FROM_IMAGE = 0;

    /**
     * Use constant material color.
     */
    public final static int COLORS_CONSTANT = 1;

    /**
     * Use the input image brightness as the bump map.
     */
    public final static int BUMPS_FROM_IMAGE = 0;

    /**
     * Use the input image alpha as the bump map.
     */
    public final static int BUMPS_FROM_IMAGE_ALPHA = 1;

    /**
     * Use a separate image alpha channel as the bump map.
     */
    public final static int BUMPS_FROM_MAP = 2;

    /**
     * Use a custom function as the bump map.
     */
    public final static int BUMPS_FROM_BEVEL = 3;
    public final static int AMBIENT = 0;
    public final static int DISTANT = 1;
    public final static int POINT = 2;
    public final static int SPOT = 3;
    protected final static float R_255 = 1.0f / 255.0f;
    Material material;
    private float bumpHeight;
    private float bumpSoftness;
    private int bumpShape;
    private float viewDistance = 10000.0f;
    private final Vector lights;
    private int colorSource = COLORS_FROM_IMAGE;
    private int bumpSource = BUMPS_FROM_IMAGE;
    private Function2D bumpFunction;
    private Image environmentMap;
    private int[] envPixels;
    private int envWidth = 1, envHeight = 1;
    private final Vector3f vector3f;
    private final Vector3f vector3f1;
    private final Vector3f vector3f2;
    private final Color4f shadedColor;
    private final Color4f diffuseColor;
    private final Color4f specularColor;
    private final Vector3f tmpV;
    private final Vector3f tmpV2;

    public ImageLightFilter() {
        lights = new Vector();
        addLight(new DistantLight());
        bumpHeight = 1.0f;
        bumpSoftness = 5.0f;
        bumpShape = 0;
        material = new Material();
        vector3f = new Vector3f();
        vector3f1 = new Vector3f();
        vector3f2 = new Vector3f();
        shadedColor = new Color4f();
        diffuseColor = new Color4f();
        specularColor = new Color4f();
        tmpV = new Vector3f();
        tmpV2 = new Vector3f();
    }

    public void addLight(Light light) {
        lights.addElement(light);
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

    public int getBumpShape() {
        return bumpShape;
    }

    public void setBumpShape(int bumpShape) {
        this.bumpShape = bumpShape;
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

    public int getColorSource() {
        return colorSource;
    }

    public void setColorSource(int colorSource) {
        this.colorSource = colorSource;
    }

    public int getDiffuseColor() {
        return material.diffuseColor;
    }

    public void setDiffuseColor(int diffuseColor) {
        material.diffuseColor = diffuseColor;
    }

    public Image getEnvironmentMap() {
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

    public Vector getLights() {
        return lights;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(float viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void removeLight(Light light) {
        lights.removeElement(light);
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
        Color4f envColor = new Color4f();
        Color4f diffuseColor = new Color4f(new Color(material.diffuseColor));
        Color4f specularColor = new Color4f(new Color(material.specularColor));
        Function2D bump = bumpFunction;

        // Apply the bump softness
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
                int[] tmpPixels = new int[bumpWidth * bumpHeight];
                int[] softPixels = new int[bumpWidth * bumpHeight];
                Kernel kernel = ImageGaussianFilter.makeKernel(bumpSoftness);
                ImageGaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, false, false, ImageGaussianFilter.WRAP_EDGES);
                ImageGaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, false, false, ImageGaussianFilter.WRAP_EDGES);
                bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
                final Function2D bbump = bump;
                if (bumpShape != 0) {
                    bump = new Function2D() {
                        private final Function2D original = bbump;

                        @Override
                        public float evaluate(float x, float y) {
                            float v = original.evaluate(x, y);
                            switch (bumpShape) {
                                case 1:
//				v = v > 0.5f ? 0.5f : v;
                                    v *= ImageMath.smoothStep(0.45f, 0.55f, v);
                                    break;
                                case 2:
                                    v = v < 0.5f ? 0.5f : v;
                                    break;
                                case 3:
                                    v = ImageMath.triangle(v);
                                    break;
                                case 4:
                                    v = ImageMath.circleDown(v);
                                    break;
                                case 5:
                                    v = ImageMath.gain(v, 0.75f);
                                    break;
                                default:
                            }
                            return v;
                        }
                    };
                }
            } else if (bumpSource != BUMPS_FROM_MAP) {
                bump = new ImageFunction2D(inPixels, width, height, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
            }
        }

        float reflectivity = material.reflectivity;
        float areflectivity = (1 - reflectivity);
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f n = new Vector3f();
        Light[] lightsArray = new Light[lights.size()];
        lights.copyInto(lightsArray);
        for (Light light : lightsArray) {
            light.prepare(width, height);
        }

        float[][] heightWindow = new float[3][width];
        for (int x = 0; x < width; x++) {
            heightWindow[1][x] = width45 * bump.evaluate(x, 0);
        }

        // Loop through each source pixel
        for (int y = 0; y < height; y++) {
            boolean y0 = y > 0;
            boolean y1 = y < height - 1;
            position.y = y;
            for (int x = 0; x < width; x++) {
                heightWindow[2][x] = width45 * bump.evaluate(x, y + 1);
            }
            for (int x = 0; x < width; x++) {
                boolean x0 = x > 0;
                boolean x1 = x < width - 1;

                // Calculate the normal at this point
                if (bumpSource != BUMPS_FROM_BEVEL) {
                    // Complicated and slower method
                    // Calculate four normals using the gradients in +/- X/Y directions
                    int count = 0;
                    normal.x = normal.y = normal.z = 0;
                    float m0 = heightWindow[1][x];
                    float m1 = x0 ? heightWindow[1][x - 1] - m0 : 0;
                    float m2 = y0 ? heightWindow[0][x] - m0 : 0;
                    float m3 = x1 ? heightWindow[1][x + 1] - m0 : 0;
                    float m4 = y1 ? heightWindow[2][x] - m0 : 0;

                    if (x0 && y1) {
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

                    if (x0 && y0) {
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

                    if (y0 && x1) {
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

                    if (x1 && y1) {
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
                    if (colorSource == COLORS_FROM_IMAGE) {
                        setFromRgb(diffuseColor, inPixels[index]);
                    } else {
                        setFromRgb(diffuseColor, material.diffuseColor);
                    }
                    if (reflectivity != 0 && environmentMap != null) {
                        tmpV2.set(viewpoint);
                        tmpV2.sub(position);
                        tmpV2.normalize();
                        tmpV.set(normal);
                        tmpV.normalize();

                        // Reflect
                        tmpV.scale(2.0f * tmpV.dot(tmpV2));
                        tmpV.sub(vector3f1);

                        tmpV.normalize();
                        setFromRgb(envColor, getEnvironmentMap(tmpV, inPixels, width, height));
                        diffuseColor.x = reflectivity * envColor.x + areflectivity * diffuseColor.x;
                        diffuseColor.y = reflectivity * envColor.y + areflectivity * diffuseColor.y;
                        diffuseColor.z = reflectivity * envColor.z + areflectivity * diffuseColor.z;
                    }
                    // Shade the pixel
                    Color4f c = phongShade(position, viewpoint, normal, diffuseColor, specularColor, material, lightsArray);
                    int alpha = inPixels[index] & 0xff000000;
                    int rgb = ((int) (c.x * 255) << 16) | ((int) (c.y * 255) << 8) | (int) (c.z * 255);
                    outPixels[index++] = alpha | rgb;
                } else {
                    outPixels[index++] = 0;
                }
            }
            float[] t = heightWindow[0];
            heightWindow[0] = heightWindow[1];
            heightWindow[1] = heightWindow[2];
            heightWindow[2] = t;
        }
        return outPixels;
    }

    protected Color4f phongShade(Vector3f position, Vector3f viewpoint, Vector3f normal, Color4f diffuseColor, Color4f specularColor, Material material, Light[] lightsArray) {
        shadedColor.set(diffuseColor);
        shadedColor.scale(material.ambientIntensity);

        for (int i = 0; i < lightsArray.length; i++) {
            Light light = lightsArray[i];
            vector3f2.set(normal);
            vector3f.set(light.position);
            if (light.type != DISTANT) {
                vector3f.sub(position);
            }
            vector3f.normalize();
            float nDotL = vector3f2.dot(vector3f);
            if (nDotL >= 0.0) {
                float dDotL = 0;

                vector3f1.set(viewpoint);
                vector3f1.sub(position);
                vector3f1.normalize();

                // Spotlight
                if (light.type == SPOT) {
                    dDotL = light.direction.dot(vector3f);
                    if (dDotL < light.cosConeAngle) {
                        continue;
                    }
                }

                vector3f2.scale(2.0f * nDotL);
                vector3f2.sub(vector3f);
                float rDotV = vector3f2.dot(vector3f1);

                float rv;
                if (rDotV < 0.0) {
                    rv = 0.0f;
                } else {
                    // Fast approximation to pow
                    rv = rDotV / (material.highlight - material.highlight * rDotV + rDotV);
                }

                // Spotlight
                if (light.type == SPOT) {
                    dDotL = light.cosConeAngle / dDotL;
                    float e = dDotL;
                    e *= e;
                    e *= e;
                    e *= e;
                    e = (float) Math.pow(dDotL, light.focus * 10) * (1 - e);
                    rv *= e;
                    nDotL *= e;
                }

                this.diffuseColor.set(diffuseColor);
                this.diffuseColor.scale(material.diffuseReflectivity);
                this.diffuseColor.x *= light.realColor.x * nDotL;
                this.diffuseColor.y *= light.realColor.y * nDotL;
                this.diffuseColor.z *= light.realColor.z * nDotL;
                this.specularColor.set(specularColor);
                this.specularColor.scale(material.specularReflectivity);
                this.specularColor.x *= light.realColor.x * rv;
                this.specularColor.y *= light.realColor.y * rv;
                this.specularColor.z *= light.realColor.z * rv;
                this.diffuseColor.add(this.specularColor);
                this.diffuseColor.clamp(0, 1);
                shadedColor.add(this.diffuseColor);
            }
        }
        shadedColor.clamp(0, 1);
        return shadedColor;
    }

    private int getEnvironmentMap(Vector3f normal, int[] inPixels, int width, int height) {
        if (environmentMap != null) {
            float angle = (float) Math.acos(-normal.y);

            float x, y;
            y = angle / ImageMath.PI;

            if (Float.valueOf(y).equals(ZERO_FLOAT) || Float.valueOf(y).equals(ONE_FLOAT)) {
                x = 0.0f;
            } else {
                float f = normal.x / (float) Math.sin(angle);

                if (f > ONE_FLOAT) {
                    f = 1.0f;
                } else if (f < NEG_ONE_FLOAT) {
                    f = -1.0f;
                }

                x = (float) Math.acos(f) / ImageMath.PI;
            }
            // A bit of empirical scaling....
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

    /**
     * A class representing material properties.
     */
    public static class Material {
        int diffuseColor;
        int specularColor;
        float ambientIntensity;
        float diffuseReflectivity;
        float specularReflectivity;
        float highlight;
        float reflectivity;
        float opacity = 1;

        public Material() {
            ambientIntensity = 0.5f;
            diffuseReflectivity = 1.0f;
            specularReflectivity = 1.0f;
            highlight = 3.0f;
            reflectivity = 0.0f;
            diffuseColor = 0xff888888;
            specularColor = 0xffffffff;
        }

        public int getDiffuseColor() {
            return diffuseColor;
        }

        public void setDiffuseColor(int diffuseColor) {
            this.diffuseColor = diffuseColor;
        }

        public float getOpacity() {
            return opacity;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

    }

    /**
     * A class representing a light.
     */
    public static class Light implements Cloneable {

        int type = AMBIENT;
        Vector3f position;
        Vector3f direction;
        Color4f realColor = new Color4f();
        int color = 0xffffffff;
        float intensity;
        float azimuth;
        float elevation;
        float focus = 0.5f;
        float centreX = 0.5f, centreY = 0.5f;
        float coneAngle = ImageMath.PI / 6;
        float cosConeAngle;
        float distance = 100.0f;

        public Light() {
            this(270 * ImageMath.PI / 180.0f, 0.5235987755982988f, 1.0f);
        }

        public Light(float azimuth, float elevation, float intensity) {
            this.azimuth = azimuth;
            this.elevation = elevation;
            this.intensity = intensity;
        }

        @Override
        public Object clone() {
            try {
                Light copy = (Light) super.clone();
                return copy;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public float getAzimuth() {
            return azimuth;
        }

        public void setAzimuth(float azimuth) {
            this.azimuth = azimuth;
        }

        /**
         * Get the centre of the light in the X direction as a proportion of the image size.
         *
         * @return the center
         * @see #setCentreX
         */
        public float getCentreX() {
            return centreX;
        }

        /**
         * Set the centre of the light in the X direction as a proportion of the image size.
         *
         * @see #getCentreX
         */
        public void setCentreX(float x) {
            centreX = x;
        }

        /**
         * Get the centre of the light in the Y direction as a proportion of the image size.
         *
         * @return the center
         * @see #setCentreY
         */
        public float getCentreY() {
            return centreY;
        }

        /**
         * Set the centre of the light in the Y direction as a proportion of the image size.
         *
         * @see #getCentreY
         */
        public void setCentreY(float y) {
            centreY = y;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getConeAngle() {
            return coneAngle;
        }

        public void setConeAngle(float coneAngle) {
            this.coneAngle = coneAngle;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public float getElevation() {
            return elevation;
        }

        public void setElevation(float elevation) {
            this.elevation = elevation;
        }

        public float getFocus() {
            return focus;
        }

        public void setFocus(float focus) {
            this.focus = focus;
        }

        public float getIntensity() {
            return intensity;
        }

        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }

        /**
         * Prepare the light for rendering.
         *
         * @param width  the output image width
         * @param height the output image height
         */
        public void prepare(int width, int height) {
            float lx = (float) (Math.cos(azimuth) * Math.cos(elevation));
            float ly = (float) (Math.sin(azimuth) * Math.cos(elevation));
            float lz = (float) Math.sin(elevation);
            direction = new Vector3f(lx, ly, lz);
            direction.normalize();
            if (type != DISTANT) {
                lx *= distance;
                ly *= distance;
                lz *= distance;
                lx += width * centreX;
                ly += height * centreY;
            }
            position = new Vector3f(lx, ly, lz);
            realColor.set(new Color(color));
            realColor.scale(intensity);
            cosConeAngle = (float) Math.cos(coneAngle);
        }


    }

    public class AmbientLight extends Light {
    }

    public class PointLight extends Light {
        public PointLight() {
            type = POINT;
        }

    }

    public class DistantLight extends Light {
        public DistantLight() {
            type = DISTANT;
        }

    }

    public class SpotLight extends Light {
        public SpotLight() {
            type = SPOT;
        }

    }
}
