package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;

/**
 * cgs2000 -> gcj02
 *
 * @author CH
 */
public class CgS2000ToGcj02CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.converterTo(GCJ02).transform(CGS2000.converterTo(WGS84).transform(point));
    }


}
