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


import com.chua.common.support.utils.ImageUtils;
import com.chua.image.support.math.Fbm;
import com.chua.image.support.math.Function2D;
import com.chua.image.support.math.Noise;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

/**
 * sky滤镜
 *
 * @author Administrator
 */
public class ImageSkyFilter extends AbstractImagePointFilter {

    private final static float R_255 = 1.0f / 255.0f;
    protected SecureRandom random = new SecureRandom();
    float mn, mx;
    private float scale = 0.1f;
    private float stretch = 1.0f;
    private float angle = 0.0f;
    private float amount = 1.0f;
    private float h = 1.0f;
    private float octaves = 8.0f;
    private float lacunarity = 2.0f;
    private float gain = 1.0f;
    private float bias = 0.6f;
    private int operation;
    private float min;
    private float max;
    private boolean ridged;
    private Fbm fBm;
    private Function2D basis;
    private float cloudCover = 0.5f;
    private float cloudSharpness = 0.5f;
    private float time = 0.3f;
    private float glow = 0.5f;
    private float glowFalloff = 0.5f;
    private float haziness = 0.96f;
    private float t = 0.0f;
    private final float sunRadius = 10f;
    private int sunColor = 0xffffffff;
    private float sunR, sunG, sunB;
    private float sunAzimuth = 0.5f;
    private float sunElevation = 0.5f;
    private float windSpeed = 0.0f;
    private float cameraAzimuth = 0.0f;
    private float cameraElevation = 0.0f;
    private float fov = 1.0f;
    private float[] exponents;
    private float[] tan;
    private BufferedImage skyColors;
    private int[] skyPixels;
    private float width, height;

    public ImageSkyFilter() {
        if (skyColors == null) {
            skyColors = ImageUtils.createImage(Toolkit.getDefaultToolkit()
                    .getImage(getClass().getResource("SkyColors.png")).getSource());
        }
    }

    public float evaluate(float x, float y) {
        float value = 0.0f;
        float remainder;
        int i;

        // to prevent "cascading" effects
        x += 371;
        y += 529;

        for (i = 0; i < (int) octaves; i++) {
            value += Noise.noise3(x, y, t) * exponents[i];
            x *= lacunarity;
            y *= lacunarity;
        }

        remainder = octaves - (int) octaves;
        if (remainder != 0) {
            value += remainder * Noise.noise3(x, y, t) * exponents[i];
        }

        return value;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        long start = System.currentTimeMillis();
        sunR = (float) ((sunColor >> 16) & 0xff) * R_255;
        sunG = (float) ((sunColor >> 8) & 0xff) * R_255;
        sunB = (float) (sunColor & 0xff) * R_255;

        mn = 10000;
        mx = -10000;
        exponents = new float[(int) octaves + 1];
        float frequency = 1.0f;
        for (int i = 0; i <= (int) octaves; i++) {
            exponents[i] = (float) Math.pow(2, -i);
            frequency *= lacunarity;
        }

        min = -1;
        max = 1;

        width = src.getWidth();
        height = src.getHeight();

        int h = src.getHeight();
        tan = new float[h];
        for (int i = 0; i < h; i++) {
            tan[i] = (float) Math.tan(fov * (float) i / h * Math.PI * 0.5);
        }

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        int t = (int) (63 * time);
        Graphics2D g = dst.createGraphics();
        g.drawImage(skyColors, 0, 0, dst.getWidth(), dst.getHeight(), t, 0, t + 1, 64, null);
        g.dispose();
        BufferedImage clouds = super.filter(dst, dst);
        long finish = System.currentTimeMillis();
        System.out.println(mn + " " + mx + " " + (finish - start) * 0.001f);
        exponents = null;
        tan = null;
        return dst;
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {

        float fx = (float) x / width;
        float fy = y / height;
        float haze = (float) Math.pow(haziness, 100 * fy * fy);
        float r = (float) ((rgb >> 16) & 0xff) * R_255;
        float g = (float) ((rgb >> 8) & 0xff) * R_255;
        float b = (float) (rgb & 0xff) * R_255;

        float cx = width * 0.5f;
        float nx = x - cx;
        float ny = y;
        ny = tan[y];
        nx = (fx - 0.5f) * (1 + ny);
        // Wind towards the camera
        ny += t * windSpeed;

        //float xscale = scale/(1+y*bias*0.1f);
        nx /= scale;
        ny /= scale * stretch;
        float f = evaluate(nx, ny);
        float fg = f;
        // Normalize to 0..1
//		f = (f-min)/(max-min);

        f = (f + 1.23f) / 2.46f;

//		f *= amount;
        int a = rgb & 0xff000000;
        int v;

        float c = f - cloudCover;
        if (c < 0) {
            c = 0;
        }

        float cloudAlpha = 1 - (float) Math.pow(cloudSharpness, c);
        mn = Math.min(mn, cloudAlpha);
        mx = Math.max(mx, cloudAlpha);

        // Sun glow
        float centreX = width * sunAzimuth;
        float centreY = height * sunElevation;
        float dx = x - centreX;
        float dy = y - centreY;
        float distance2 = dx * dx + dy * dy;
        distance2 = (float) Math.pow(distance2, glowFalloff);
        float sun = 10 * (float) Math.exp(-distance2 * glow * 0.1f);

        r += sun * sunR;
        g += sun * sunG;
        b += sun * sunB;
        float ca = (1 - cloudAlpha * cloudAlpha * cloudAlpha * cloudAlpha) * amount;
        float cloudR = sunR * ca;
        float cloudG = sunG * ca;
        float cloudB = sunB * ca;

        // Apply the haziness as we move further away
        cloudAlpha *= haze;

        // Composite the clouds on the sky
        float iCloudAlpha = (1 - cloudAlpha);
        r = iCloudAlpha * r + cloudAlpha * cloudR;
        g = iCloudAlpha * g + cloudAlpha * cloudG;
        b = iCloudAlpha * b + cloudAlpha * cloudB;

        // Exposure
        float exposure = gain;
        r = 1 - (float) Math.exp(-r * exposure);
        g = 1 - (float) Math.exp(-g * exposure);
        b = 1 - (float) Math.exp(-b * exposure);

        int ir = (int) (255 * r) << 16;
        int ig = (int) (255 * g) << 8;
        int ib = (int) (255 * b);
        v = 0xff000000 | ir | ig | ib;
        return v;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }

    public float getCameraAzimuth() {
        return cameraAzimuth;
    }

    public void setCameraAzimuth(float cameraAzimuth) {
        this.cameraAzimuth = cameraAzimuth;
    }

    public float getCameraElevation() {
        return cameraElevation;
    }

    public void setCameraElevation(float cameraElevation) {
        this.cameraElevation = cameraElevation;
    }

    public float getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(float cloudCover) {
        this.cloudCover = cloudCover;
    }

    public float getCloudSharpness() {
        return cloudSharpness;
    }

    public void setCloudSharpness(float cloudSharpness) {
        this.cloudSharpness = cloudSharpness;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public float getGlow() {
        return glow;
    }

    public void setGlow(float glow) {
        this.glow = glow;
    }

    public float getGlowFalloff() {
        return glowFalloff;
    }

    public void setGlowFalloff(float glowFalloff) {
        this.glowFalloff = glowFalloff;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public float getHaziness() {
        return haziness;
    }

    public void setHaziness(float haziness) {
        this.haziness = haziness;
    }

    public float getLacunarity() {
        return lacunarity;
    }

    public void setLacunarity(float lacunarity) {
        this.lacunarity = lacunarity;
    }

    public float getOctaves() {
        return octaves;
    }

    public void setOctaves(float octaves) {
        this.octaves = octaves;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getStretch() {
        return stretch;
    }

    public void setStretch(float stretch) {
        this.stretch = stretch;
    }

    public float getSunAzimuth() {
        return sunAzimuth;
    }

    public void setSunAzimuth(float sunAzimuth) {
        this.sunAzimuth = sunAzimuth;
    }

    public int getSunColor() {
        return sunColor;
    }

    public void setSunColor(int sunColor) {
        this.sunColor = sunColor;
    }

    public float getSunElevation() {
        return sunElevation;
    }

    public void setSunElevation(float sunElevation) {
        this.sunElevation = sunElevation;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

}
