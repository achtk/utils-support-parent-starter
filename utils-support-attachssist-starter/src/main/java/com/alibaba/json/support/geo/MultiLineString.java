package com.alibaba.json.support.geo;

import com.alibaba.json.annotation.JSONType;

/**
 * @since 1.2.68
 */
@JSONType(typeName = "MultiLineString", orders = {"type", "bbox", "coordinates"})
public class MultiLineString extends Geometry {
    private double[][][] coordinates;

    public MultiLineString() {
        super("MultiLineString");
    }

    public double[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][] coordinates) {
        this.coordinates = coordinates;
    }
}
