package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * gcj02 -> bd09
 *
 * @author CH
 */
public class Gcj02ToBd09CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double[] doubles = GeoUtils.transformGcJ02ToBd09(point.longitude(), point.latitude());
        return Point.builder().longitude(doubles[0]).latitude(doubles[1]).build();
    }
}
