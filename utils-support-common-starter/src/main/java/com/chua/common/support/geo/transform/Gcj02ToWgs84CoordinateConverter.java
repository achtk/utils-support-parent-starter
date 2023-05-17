package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * gcj02 -> wgs84
 *
 * @author CH
 */
public class Gcj02ToWgs84CoordinateConverter implements CoordinateConverter {

    @Override
    public Point transform(Point point) {
        double[] doubles = GeoUtils.transformGcJ02ToWgS84(point.longitude(), point.latitude());
        return Point.builder().longitude(doubles[0]).latitude(doubles[1]).build();
    }
}
