package com.chua.agent.support.json.support.geo;

import com.chua.agent.support.json.annotation.JSONType;

/**
 * @since 1.2.68
 */
@JSONType(typeName = "MultiPoint", orders = {"type", "bbox", "coordinates"})
public class MultiPoint extends Geometry {
    private double[][] coordinates;

    public MultiPoint() {
        super("MultiPoint");
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }
}
