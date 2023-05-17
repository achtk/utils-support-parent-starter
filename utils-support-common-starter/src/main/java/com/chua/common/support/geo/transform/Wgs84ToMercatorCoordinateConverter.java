package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * wgs84 -> mercator
 *
 * @author CH
 */
public class Wgs84ToMercatorCoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double x = point.longitude() * 20037508.342789 / 180;
        double y = Math.log(Math.tan((90 + point.latitude()) * GeoUtils.PI / 360)) / (GeoUtils.PI / 180);
        y = y * 20037508.342789 / 180;
        return Point.builder().longitude(x).latitude(y).build();
    }
}
