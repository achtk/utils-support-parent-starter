package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.GCJ02;
import static com.chua.common.support.geo.transform.Coordinate.WGS84;

/**
 * mercator -> gcj02
 *
 * @author CH
 */
public class MercatorToGcj02CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.converterTo(GCJ02).transform(Coordinate.MERCATOR.converterTo(Coordinate.WGS84).transform(point));
    }
}
