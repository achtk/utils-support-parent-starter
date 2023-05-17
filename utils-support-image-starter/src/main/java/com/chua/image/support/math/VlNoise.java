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

/**
 * vl噪声
 *
 * @author CH
 */
public class VlNoise implements Function2D {

    private float distortion = 10.0f;

    @Override
    public float evaluate(float x, float y) {
        float ox = Noise.noise2(x + 0.5f, y) * distortion;
        float oy = Noise.noise2(x, y + 0.5f) * distortion;
        return Noise.noise2(x + ox, y + oy);
    }

    public float getDistortion() {
        return distortion;
    }

    public void setDistortion(float distortion) {
        this.distortion = distortion;
    }

}
