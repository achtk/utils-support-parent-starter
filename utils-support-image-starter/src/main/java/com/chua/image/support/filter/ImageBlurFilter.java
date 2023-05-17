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
 * 一个简单的模糊过滤器。您可能应该改用 BoxBlurFilter。
 *
 * @author CH
 */
public class ImageBlurFilter extends ImageConvolveFilter {

    /**
     * A 3x3 convolution kernel for a simple blur.
     */
    protected static final float[] BLUR_MATRIX = {
            1 / 14f, 2 / 14f, 1 / 14f,
            2 / 14f, 2 / 14f, 2 / 14f,
            1 / 14f, 2 / 14f, 1 / 14f
    };

    public ImageBlurFilter() {
        super(BLUR_MATRIX);
    }

}
