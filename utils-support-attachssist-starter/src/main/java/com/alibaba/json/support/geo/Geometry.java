package com.alibaba.json.support.geo;

import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.support.geo.Feature;
import com.alibaba.json.support.geo.GeometryCollection;
import com.alibaba.json.support.geo.LineString;
import com.alibaba.json.support.geo.MultiLineString;
import com.alibaba.json.support.geo.MultiPoint;
import com.alibaba.json.support.geo.MultiPolygon;
import com.alibaba.json.support.geo.Point;
import com.alibaba.json.support.geo.Polygon;

/**
 * @since 1.2.68
 */
@JSONType(seeAlso = {GeometryCollection.class
        , LineString.class
        , MultiLineString.class
        , Point.class
        , MultiPoint.class
        , Polygon.class
        , MultiPolygon.class
        , Feature.class
        , FeatureCollection.class}
    , typeKey = "type")
public abstract class Geometry {
    private final String type;
    private double[] bbox;

    protected Geometry(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }
}
