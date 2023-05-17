package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

import static com.chua.common.support.geo.transform.Coordinate.BD09;
import static com.chua.common.support.geo.transform.Coordinate.WGS84;

/**
 * mercator -> bd09
 *
 * @author CH
 */
public class MercatorToBd09CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        return WGS84.converterTo(BD09).transform(Coordinate.MERCATOR.converterTo(Coordinate.WGS84).transform(point));
    }
}
