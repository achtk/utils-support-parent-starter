package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.*;

/**
 * cgs2000 -> bd09
 *
 * @author CH
 */
public class CgS2000ToBd09CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.converterTo(BD09).transform(CGS2000.converterTo(WGS84).transform(point));
    }


}
