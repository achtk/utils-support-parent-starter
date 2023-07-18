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
import com.chua.image.support.utils.ImageMath;
import com.chua.image.support.utils.PixelUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * This filter applies a marbling effect to an image, displacing pixels by random amounts.
 * 该滤镜对图像应用大理石花纹效果，随机置换像素。
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Spi("Marble")
@SpiOption("大理石花纹滤镜")
public class ImageMarbleFilter extends AbstractImageTransformFilter {

    private float[] sinTable, cosTable;
    private float xScale = 4;
    private float yScale = 4;
    private float amount = 1;
    private float turbulence = 1;

    public ImageMarbleFilter() {
        setEdgeAction(CLAMP);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        initialize();
        return super.filter(src, dst);
    }

    private void initialize() {
        sinTable = new float[256];
        cosTable = new float[256];
        for (int i = 0; i < MAX_256; i++) {
            float angle = ImageMath.TWO_PI * i / 256f * turbulence;
            sinTable[i] = (float) (-yScale * Math.sin(angle));
            cosTable[i] = (float) (yScale * Math.cos(angle));
        }
    }

    private int displacementMap(int x, int y) {
        return PixelUtils.clamp((int) (127 * (1 + Noise.noise2(x / xScale, y / xScale))));
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        int displacement = displacementMap(x, y);
        out[0] = x + sinTable[displacement];
        out[1] = y + cosTable[displacement];
    }

}
