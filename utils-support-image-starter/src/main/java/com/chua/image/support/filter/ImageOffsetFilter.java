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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * 偏移滤镜
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Spi("Offset")
@SpiOption("偏移滤镜")
public class ImageOffsetFilter extends AbstractImageTransformFilter {

    private int width, height;
    private int xOffset, yOffset;
    private boolean wrap;

    public ImageOffsetFilter() {
        this(0, 0, true);
    }

    public ImageOffsetFilter(int xOffset, int yOffset, boolean wrap) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.wrap = wrap;
        setEdgeAction(ZERO);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        this.width = src.getWidth();
        this.height = src.getHeight();
        if (wrap) {
            while (xOffset < 0) {
                xOffset += width;
            }
            while (yOffset < 0) {
                yOffset += height;
            }
            xOffset %= width;
            yOffset %= height;
        }
        return super.filter(src, dst);
    }


    @Override
    protected void transformInverse(int x, int y, float[] out) {
        if (wrap) {
            out[0] = (x + width - xOffset) % width;
            out[1] = (y + height - yOffset) % height;
        } else {
            out[0] = x - xOffset;
            out[1] = y - yOffset;
        }
    }
}
