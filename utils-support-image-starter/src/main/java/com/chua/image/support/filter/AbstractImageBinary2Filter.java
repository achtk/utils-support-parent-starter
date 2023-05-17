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

import com.chua.image.support.function.BinaryFunction;
import com.chua.image.support.function.BlackFunction;
import com.chua.image.support.map.Colormap;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一些处理二进制图像的过滤器的超类。
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractImageBinary2Filter extends ImageWholeImageFilter {

    protected int newColor = 0xff000000;
    protected BinaryFunction blackFunction = new BlackFunction();
    protected int iterations = 1;
    protected Colormap colormap;


}

