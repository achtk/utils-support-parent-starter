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


import com.chua.image.support.math.Noise;
import com.chua.image.support.utils.ImageMath;

import java.awt.*;

/**
 * A filter which distorts an image by rippling it in the X or Y directions.
 * The amplitude and wavelength of rippling can be specified as well as whether
 * pixels going off the edges are wrapped or not.
 * 波纹滤镜
 *
 * @author Administrator
 */
public class ImageRippleFilter extends AbstractImageTransformFilter {

    /**
     * Sine wave ripples.
     */
    public final static int SINE = 0;

    /**
     * Sawtooth wave ripples.
     */
    public final static int SAWTOOTH = 1;

    /**
     * Triangle wave ripples.
     */
    public final static int TRIANGLE = 2;

    /**
     * Noise ripples.
     */
    public final static int NOISE = 3;

    private float amplitudeX, amplitudeY;
    private float wavelengthX, wavelengthY;
    private int waveType;

    /**
     * Construct a RippleFilter.
     */
    public ImageRippleFilter() {
        amplitudeX = 5.0f;
        amplitudeY = 0.0f;
        wavelengthX = wavelengthY = 16.0f;
    }

    /**
     * Get the amplitude of ripple in the X direction.
     *
     * @return the amplitude (in pixels).
     * @see #setAmplitudeA
     */
    public float getAmplitudeX() {
        return amplitudeX;
    }

    /**
     * Get the amplitude of ripple in the Y direction.
     *
     * @return the amplitude (in pixels).
     * @see #setAmplitudeY
     */
    public float getAmplitudeY() {
        return amplitudeY;
    }

    /**
     * Set the amplitude of ripple in the Y direction.
     *
     * @param yAmplitude the amplitude (in pixels).
     * @see #getAmplitudeY
     */
    public void setAmplitudeY(float yAmplitude) {
        this.amplitudeY = yAmplitude;
    }

    /**
     * Get the wave type.
     *
     * @return the type.
     * @see #setWaveType
     */
    public int getWaveType() {
        return waveType;
    }

    /**
     * Set the wave type.
     *
     * @param waveType the type.
     * @see #getWaveType
     */
    public void setWaveType(int waveType) {
        this.waveType = waveType;
    }

    /**
     * Get the wavelength of ripple in the X direction.
     *
     * @return the wavelength (in pixels).
     * @see #setWavelengthX
     */
    public float getWavelengthX() {
        return wavelengthX;
    }

    /**
     * Set the wavelength of ripple in the X direction.
     *
     * @param xWavelength the wavelength (in pixels).
     * @see #getWavelengthX
     */
    public void setWavelengthX(float xWavelength) {
        this.wavelengthX = xWavelength;
    }

    /**
     * Get the wavelength of ripple in the Y direction.
     *
     * @return the wavelength (in pixels).
     * @see #setWavelengthY
     */
    public float getWavelengthY() {
        return wavelengthY;
    }

    /**
     * Set the wavelength of ripple in the Y direction.
     *
     * @param yWavelength the wavelength (in pixels).
     * @see #getWavelengthY
     */
    public void setWavelengthY(float yWavelength) {
        this.wavelengthY = yWavelength;
    }

    /**
     * Set the amplitude of ripple in the X direction.
     *
     * @param xAmplitude the amplitude (in pixels).
     * @see #getAmplitudeX
     */
    public void setAmplitudeA(float xAmplitude) {
        this.amplitudeX = xAmplitude;
    }

    @Override
    protected void transformSpace(Rectangle r) {
        if (edgeAction == ZERO) {
            r.x -= (int) amplitudeX;
            r.width += (int) (2 * amplitudeX);
            r.y -= (int) amplitudeY;
            r.height += (int) (2 * amplitudeY);
        }
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        float nx = (float) y / wavelengthX;
        float ny = (float) x / wavelengthY;
        float fx, fy;
        switch (waveType) {
            case SINE:
            default:
                fx = (float) Math.sin(nx);
                fy = (float) Math.sin(ny);
                break;
            case SAWTOOTH:
                fx = ImageMath.mod(nx, 1);
                fy = ImageMath.mod(ny, 1);
                break;
            case TRIANGLE:
                fx = ImageMath.triangle(nx);
                fy = ImageMath.triangle(ny);
                break;
            case NOISE:
                fx = Noise.noise1(nx);
                fy = Noise.noise1(ny);
                break;
        }
        out[0] = x + amplitudeX * fx;
        out[1] = y + amplitudeY * fy;
    }

}
