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

package com.chua.image.support.composite;

import java.awt.*;
import java.awt.image.ColorModel;

import static com.chua.common.support.constant.NumberConstant.FOUR;

/**
 * 灯光复合
 *
 * @author Administrator
 */
public final class PinLightComposite extends AbstractRgbComposite {

    public PinLightComposite(float alpha) {
        super(alpha);
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new Context(extraAlpha, srcColorModel, dstColorModel);
    }

    static class Context extends AbstractRgbCompositeContext {
        public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
            super(alpha, srcColorModel, dstColorModel);
        }

        @Override
        public void compose(int[] src, int[] dst, float alpha) {
            int w = src.length;

            for (int i = 0; i < w; i += FOUR) {
                int sr = src[i];
                int dir = dst[i];
                int sg = src[i + 1];
                int dig = dst[i + 1];
                int sb = src[i + 2];
                int dib = dst[i + 2];
                int sa = src[i + 3];
                int dia = dst[i + 3];
                int dor, dog, dob;

                dor = sr > 127 ? Math.max(sr, dir) : Math.min(sr, dir);
                dog = sg > 127 ? Math.max(sg, dig) : Math.min(sg, dig);
                dob = sb > 127 ? Math.max(sb, dib) : Math.min(sb, dib);

                float a = alpha * sa / 255f;
                float ac = 1 - a;

                dst[i] = (int) (a * dor + ac * dir);
                dst[i + 1] = (int) (a * dog + ac * dig);
                dst[i + 2] = (int) (a * dob + ac * dib);
                dst[i + 3] = (int) (sa * alpha + dia * ac);
            }
        }
    }

}
