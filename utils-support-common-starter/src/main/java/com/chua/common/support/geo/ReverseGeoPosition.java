package com.chua.common.support.geo;

/**
 * 逆物理地址解析
 *
 * @author CH
 */
public interface ReverseGeoPosition {
    /**
     * 逆地址
     *
     * @param latitude  经度
     * @param longitude 纬度
     * @return 地址
     */
    GeoCity nearestPlace(double latitude, double longitude);
}
