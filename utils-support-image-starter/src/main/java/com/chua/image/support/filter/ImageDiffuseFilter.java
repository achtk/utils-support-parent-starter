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
import com.chua.image.support.utils.ImageMath;

import java.awt.image.BufferedImage;

/**
 * 这个过滤器通过在随机方向上移动像素来扩散图像。
 *
 * @author Administrator
 */
@Spi("Diffuse")
@SpiOption("随机扩散滤镜")
public class ImageDiffuseFilter extends AbstractImageTransformFilter {

    private float[] sinTable, cosTable;
    private float scale = 4;

    public ImageDiffuseFilter() {
        setEdgeAction(CLAMP);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        sinTable = new float[256];
        cosTable = new float[256];
        int max = 256;
        for (int i = 0; i < max; i++) {
            float angle = ImageMath.TWO_PI * i / 256f;
            sinTable[i] = (float) (scale * Math.sin(angle));
            cosTable[i] = (float) (scale * Math.cos(angle));
        }
        return super.filter(src, dst);
    }

    /**
     * Returns the scale of the texture.
     *
     * @return the scale of the texture.
     * @see #setScale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Specifies the scale of the texture.
     *
     * @param scale the scale of the texture.
     * @min-value 1
     * @max-value 100+
     * @see #getScale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        int angle = (int) (Math.random() * 255D);
        float distance = (float) Math.random();
        out[0] = x + distance * sinTable[angle];
        out[1] = y + distance * cosTable[angle];
    }
}
