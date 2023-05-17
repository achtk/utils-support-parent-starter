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
 * Applies a bit mask to each ARGB pixel of an image. You can use this for, say, masking out the red channel.
 *
 * @author Administrator
 */
public class ImageMaskFilter extends AbstractImagePointFilter {

    private int mask;

    public ImageMaskFilter() {
        this(0xff00ffff);
    }

    public ImageMaskFilter(int mask) {
        canFilterIndexColorModel = true;
        setMask(mask);
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        return rgb & mask;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }


}
