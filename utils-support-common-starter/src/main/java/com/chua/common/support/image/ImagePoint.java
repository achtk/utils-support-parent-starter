package com.chua.common.support.protocol.image;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

/**
 * 图像
 *
 * @author CH
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImagePoint extends Point {
    private double rate;
    private int width;
    private int height;

    public ImagePoint(double rate) {
        this.rate = rate;
    }

    public ImagePoint(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ImagePoint(Point p, int width, int height) {
        super(p);
        this.width = width;
        this.height = height;
    }

    public ImagePoint(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }
}
