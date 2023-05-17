package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;


/**
 * bd09 -> cgs2000
 *
 * @author CH
 */
public class Bd09ToCgS2000CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.to(CGS2000, BD09.converterTo(WGS84).transform(point));
    }
}
