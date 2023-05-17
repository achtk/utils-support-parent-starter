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


import com.chua.image.support.utils.ImageMath;

/**
 * 编织滤镜
 *
 * @author Administrator
 */
public class ImageWeaveFilter extends AbstractImagePointFilter {

    public int[][] matrix = {
            {0, 1, 0, 1},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {1, 0, 1, 0},
    };
    private float widthX = 16;
    private float widthY = 16;
    private float gapX = 6;
    private float gapY = 6;
    private final int rows = 4;
    private final int cols = 4;
    private final int rgbX = 0xffff8080;
    private final int rgbY = 0xff8080ff;
    private boolean useImageColors = true;
    private boolean roundThreads = false;
    private boolean shadeCrossings = true;

    public ImageWeaveFilter() {
    }

    @Override
    public int filterRgb(int x, int y, int rgb) {
        x += widthX + gapX / 2;
        y += widthY + gapY / 2;
        float nx = ImageMath.mod(x, widthX + gapX);
        float ny = ImageMath.mod(y, widthY + gapY);
        int ix = (int) (x / (widthX + gapX));
        int iy = (int) (y / (widthY + gapY));
        boolean inX = nx < widthX;
        boolean inY = ny < widthY;
        float dX, dY;
        float cX, cY;
        int lrgbX, lrgbY;

        if (roundThreads) {
            dX = Math.abs(widthX / 2 - nx) / widthX / 2;
            dY = Math.abs(widthY / 2 - ny) / widthY / 2;
        } else {
            dX = dY = 0;
        }

        if (shadeCrossings) {
            cX = ImageMath.smoothStep(widthX / 2, widthX / 2 + gapX, Math.abs(widthX / 2 - nx));
            cY = ImageMath.smoothStep(widthY / 2, widthY / 2 + gapY, Math.abs(widthY / 2 - ny));
        } else {
            cX = cY = 0;
        }

        if (useImageColors) {
            lrgbX = lrgbY = rgb;
        } else {
            lrgbX = rgbX;
            lrgbY = rgbY;
        }
        int v;
        int ixc = ix % cols;
        int iyr = iy % rows;
        int m = matrix[iyr][ixc];
        if (inX) {
            if (inY) {
                v = m == 1 ? lrgbX : lrgbY;
                v = ImageMath.mixColors(2 * (m == 1 ? dX : dY), v, 0xff000000);
            } else {
                if (shadeCrossings) {
                    if (m != matrix[(iy + 1) % rows][ixc]) {
                        if (m == 0) {
                            cY = 1 - cY;
                        }
                        cY *= 0.5f;
                        lrgbX = ImageMath.mixColors(cY, lrgbX, 0xff000000);
                    } else if (m == 0) {
                        lrgbX = ImageMath.mixColors(0.5f, lrgbX, 0xff000000);
                    }
                }
                v = ImageMath.mixColors(2 * dX, lrgbX, 0xff000000);
            }
        } else if (inY) {
            if (shadeCrossings) {
                if (m != matrix[iyr][(ix + 1) % cols]) {
                    if (m == 1) {
                        cX = 1 - cX;
                    }
                    cX *= 0.5f;
                    lrgbY = ImageMath.mixColors(cX, lrgbY, 0xff000000);
                } else if (m == 1) {
                    lrgbY = ImageMath.mixColors(0.5f, lrgbY, 0xff000000);
                }
            }
            v = ImageMath.mixColors(2 * dY, lrgbY, 0xff000000);
        } else {
            v = 0x00000000;
        }
        return v;
    }

    public int[][] getCrossings() {
        return matrix;
    }

    public void setCrossings(int[][] matrix) {
        this.matrix = matrix;
    }

    public boolean getRoundThreads() {
        return roundThreads;
    }

    public void setRoundThreads(boolean roundThreads) {
        this.roundThreads = roundThreads;
    }

    public boolean getShadeCrossings() {
        return shadeCrossings;
    }

    public void setShadeCrossings(boolean shadeCrossings) {
        this.shadeCrossings = shadeCrossings;
    }

    public boolean getUseImageColors() {
        return useImageColors;
    }

    public void setUseImageColors(boolean useImageColors) {
        this.useImageColors = useImageColors;
    }

    public float getGapX() {
        return gapX;
    }

    public void setGapX(float xGap) {
        this.gapX = xGap;
    }

    public float getWidthX() {
        return widthX;
    }

    public void setWidthX(float xWidth) {
        this.widthX = xWidth;
    }

    public float getGapY() {
        return gapY;
    }

    public void setGapY(float yGap) {
        this.gapY = yGap;
    }

    public float getWidthY() {
        return widthY;
    }

    public void setWidthY(float yWidth) {
        this.widthY = yWidth;
    }

}


