package com.chua.agent.support.json.support.geo;

import com.chua.agent.support.json.annotation.JSONType;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.2.68
 */
@JSONType(typeName = "FeatureCollection", orders = {"type", "bbox", "coordinates"})
public class FeatureCollection
        extends Geometry {
    private List<Feature> features = new ArrayList<Feature>();

    public FeatureCollection() {
        super("FeatureCollection");
    }

    public List<Feature> getFeatures() {
        return features;
    }
}
