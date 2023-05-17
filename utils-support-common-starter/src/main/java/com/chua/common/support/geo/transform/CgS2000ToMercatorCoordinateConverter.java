package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;

/**
 * cgs2000 -> mercator
 *
 * @author CH
 */
public class CgS2000ToMercatorCoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.converterTo(MERCATOR).transform(CGS2000.converterTo(WGS84).transform(point));
    }


}
