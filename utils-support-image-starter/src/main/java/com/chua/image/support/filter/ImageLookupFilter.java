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

import com.chua.image.support.map.Colormap;
import com.chua.image.support.map.Gradient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A filter which uses the brightness of each pixel to lookup a color from a colormap.
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageLookupFilter extends AbstractImagePointFilter {

    private Colormap colormap = new Gradient();

    /**
     * Construct a LookupFilter.
     */
    public ImageLookupFilter() {
        canFilterIndexColorModel = true;
    }

    /**
     * Construct a LookupFilter.
     *
     * @param colormap the color map
     */
    public ImageLookupFilter(Colormap colormap) {
        canFilterIndexColorModel = true;
        this.colormap = colormap;
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        rgb = (r + g + b) / 3;
        return colormap.getColor(rgb / 255.0f);
    }


}


