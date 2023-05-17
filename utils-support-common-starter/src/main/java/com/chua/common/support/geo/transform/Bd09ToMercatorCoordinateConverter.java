package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

/**
 * bd09 -> mercator
 *
 * @author CH
 */
public class Bd09ToMercatorCoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return Coordinate.WGS84.converterTo(Coordinate.MERCATOR).transform(Coordinate.BD09.to(Coordinate.WGS84, point));
    }
}
