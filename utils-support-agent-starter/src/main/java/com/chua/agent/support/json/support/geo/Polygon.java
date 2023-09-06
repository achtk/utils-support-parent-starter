package com.chua.agent.support.json.support.geo;

import com.chua.agent.support.json.annotation.JSONType;

/**
 * @since 1.2.68
 */
@JSONType(typeName = "Polygon", orders = {"type", "bbox", "coordinates"})
public class Polygon extends Geometry {
    private double[][][] coordinates;

    public Polygon() {
        super("Polygon");
    }

    public double[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][] coordinates) {
        this.coordinates = coordinates;
    }
}
