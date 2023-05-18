package com.alibaba.json.support.geo;

import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.support.geo.Feature;
import com.alibaba.json.support.geo.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.2.68
 */
@JSONType(typeName = "FeatureCollection", orders = {"type", "bbox", "coordinates"})
public class FeatureCollection
        extends Geometry {
    private List<com.alibaba.json.support.geo.Feature> features = new ArrayList<com.alibaba.json.support.geo.Feature>();

    public FeatureCollection() {
        super("FeatureCollection");
    }

    public List<Feature> getFeatures() {
        return features;
    }
}
