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

import java.awt.*;

/**
 * 剪切滤镜
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Spi("Shear")
@SpiOption("剪切滤镜")
public class ImageShearFilter extends AbstractImageTransformFilter {

    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private float shx = 0.0f;
    private float shy = 0.0f;
    private float xOffset = 0.0f;
    private float yOffset = 0.0f;
    private boolean resize = true;

    public ImageShearFilter() {
    }


    public void setAngleX(float xangle) {
        this.angleX = xangle;
        initialize();
    }


    public void setAngleY(float yangle) {
        this.angleY = yangle;
        initialize();
    }


    private void initialize() {
        shx = (float) Math.sin(angleX);
        shy = (float) Math.sin(angleY);
    }

    @Override
    protected void transformSpace(Rectangle r) {
        float tangent = (float) Math.tan(angleX);
        xOffset = -r.height * tangent;
        if (tangent < 0.0) {
            tangent = -tangent;
        }
        r.width = (int) (r.height * tangent + r.width + 0.999999f);
        tangent = (float) Math.tan(angleY);
        yOffset = -r.width * tangent;
        if (tangent < 0.0) {
            tangent = -tangent;
        }
        r.height = (int) (r.width * tangent + r.height + 0.999999f);
    }


    @Override
    protected void transformInverse(int x, int y, float[] out) {
        out[0] = x + xOffset + (y * shx);
        out[1] = y + yOffset + (x * shy);
    }


}
