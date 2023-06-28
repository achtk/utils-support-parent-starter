package com.chua.common.support.constant;

import lombok.Data;

import java.awt.*;
import java.util.List;

/**
 * boundingBox
 * @author CH
 */
@Data
public class BoundingBox {
    private List<Point> corners;

    private double width;
    private double height;
}
