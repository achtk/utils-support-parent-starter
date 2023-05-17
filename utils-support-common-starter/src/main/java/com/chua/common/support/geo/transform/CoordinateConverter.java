package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.Point;

/**
 * 转化
 *
 * @author CH
 */
public interface CoordinateConverter {
    /**
     * 转化
     *
     * @param point 点位
     * @return 结果
     */
    Point transform(Point point);
}
