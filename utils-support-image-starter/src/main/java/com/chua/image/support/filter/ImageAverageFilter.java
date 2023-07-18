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

/**
 * 平均每个像素的 3x3 邻域的过滤器，提供简单的模糊。
 *
 * @author CH
 */
@Spi("Average")
@SpiOption("简单模糊")
public class ImageAverageFilter extends ImageConvolveFilter {

    /**
     * 用于平均的卷积核。
     */
    protected static float[] theMatrix = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};

    public ImageAverageFilter() {
        super(theMatrix);
    }

}
