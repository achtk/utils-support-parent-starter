package com.chua.common.support.constant;

import com.chua.common.support.pojo.Shape;
import lombok.Data;

import java.util.List;

/**
 * boundingBox
 * @author CH
 */
@Data
public class BoundingBox {
    private List<Shape> corners;

    private double width;
    private double height;
}
