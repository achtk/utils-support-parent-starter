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

/**
 * A filter for changing the gamma of an image.
 * γ滤镜
 *
 * @author Administrator
 */
public class ImageGammaFilter extends AbstractImageTransferFilter {

    private float rGamma, gGamma, bGamma;

    /**
     * Construct a GammaFilter.
     */
    public ImageGammaFilter() {
        this(1.0f);
    }

    /**
     * Construct a GammaFilter.
     *
     * @param gamma the gamma level for all RGB channels
     */
    public ImageGammaFilter(float gamma) {
        this(gamma, gamma, gamma);
    }

    /**
     * Construct a GammaFilter.
     *
     * @param rGamma the gamma level for the red channel
     * @param gGamma the gamma level for the blue channel
     * @param bGamma the gamma level for the green channel
     */
    public ImageGammaFilter(float rGamma, float gGamma, float bGamma) {
        setGamma(rGamma, gGamma, bGamma);
    }

    /**
     * Get the gamma level.
     *
     * @return the gamma level for all RGB channels
     * @see #setGamma
     */
    public float getGamma() {
        return rGamma;
    }

    /**
     * Set the gamma level.
     *
     * @param gamma the gamma level for all RGB channels
     * @see #getGamma
     */
    public void setGamma(float gamma) {
        setGamma(gamma, gamma, gamma);
    }

    /**
     * Set the gamma levels.
     *
     * @param rGamma the gamma level for the red channel
     * @param gGamma the gamma level for the blue channel
     * @param bGamma the gamma level for the green channel
     * @see #getGamma
     */
    public void setGamma(float rGamma, float gGamma, float bGamma) {
        this.rGamma = rGamma;
        this.gGamma = gGamma;
        this.bGamma = bGamma;
        initialized = false;
    }

    @Override
    protected void initialize() {
        rTable = makeTable(rGamma);

        if (Float.valueOf(gGamma).equals(rGamma)) {
            gTable = rTable;
        } else {
            gTable = makeTable(gGamma);
        }

        if (Float.valueOf(bGamma).equals(rGamma)) {
            bTable = rTable;
        } else if (Float.valueOf(bGamma).equals(gGamma)) {
            bTable = gTable;
        } else {
            bTable = makeTable(bGamma);
        }
    }

    private int[] makeTable(float gamma) {
        int[] table = new int[256];
        int max = 256;
        for (int i = 0; i < max; i++) {
            int v = (int) ((255.0 * Math.pow(i / 255.0, 1.0 / gamma)) + 0.5);
            if (v > 255) {
                v = 255;
            }
            table[i] = v;
        }
        return table;
    }

}

