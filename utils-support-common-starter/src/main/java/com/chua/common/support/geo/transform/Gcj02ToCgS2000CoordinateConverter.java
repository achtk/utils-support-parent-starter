package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;

/**
 * gcj02 -> cgs2000
 *
 * @author CH
 */
public class Gcj02ToCgS2000CoordinateConverter implements CoordinateConverter {

    @Override
    public Point transform(Point point) {
        return WGS84.to(CGS2000, GCJ02.converterTo(WGS84).transform(point));
    }
}
