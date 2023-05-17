package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * wgs84 -> gcj02
 *
 * @author CH
 */
public class Wgs84ToGcj02CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double[] doubles = GeoUtils.transformWgs84ToGcJ02(point.longitude(), point.latitude());
        return Point.builder().longitude(doubles[0]).latitude(doubles[1]).build();
    }
}
