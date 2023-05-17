package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;

/**
 * mercator -> cgs2000
 *
 * @author CH
 */
public class MercatorToCgS2000CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.to(CGS2000, MERCATOR.converterTo(WGS84).transform(point));
    }
}
