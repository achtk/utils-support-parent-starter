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

package com.chua.image.support.math;

import static com.chua.common.support.constant.NumberConstant.TWE;

/**
 * 函数
 *
 * @author CH
 */
public class TurbulenceFunction extends AbstractCompoundFunction2D {

    private float octaves;

    public TurbulenceFunction(Function2D basis, float octaves) {
        super(basis);
        this.octaves = octaves;
    }

    @Override
    public float evaluate(float x, float y) {
        float t = 0.0f;

        for (float f = 1.0f; f <= octaves; f *= TWE) {
            t += Math.abs(basis.evaluate(f * x, f * y)) / f;
        }
        return t;
    }

    public float getOctaves() {
        return octaves;
    }

    public void setOctaves(float octaves) {
        this.octaves = octaves;
    }

}
