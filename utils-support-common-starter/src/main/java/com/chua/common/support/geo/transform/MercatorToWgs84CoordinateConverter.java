package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * mercator -> wgs84
 *
 * @author CH
 */
public class MercatorToWgs84CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double x = point.longitude() / 20037508.34 * 180;
        double y = point.latitude() / 20037508.34 * 180;
        y = 180 / GeoUtils.PI * (2 * Math.atan(Math.exp(y * GeoUtils.PI / 180)) - GeoUtils.PI / 2);
        return Point.builder().longitude(x).latitude(y).build();
    }
}
