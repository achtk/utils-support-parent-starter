package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

/**
 * gcj02 -> mercator
 *
 * @author CH
 */
public class Gcj02ToMercatorCoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return Coordinate.WGS84.converterTo(Coordinate.MERCATOR).transform(Coordinate.GCJ02.to(Coordinate.WGS84, point));
    }
}
