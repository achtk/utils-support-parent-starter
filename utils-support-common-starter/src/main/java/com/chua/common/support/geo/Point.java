package com.chua.common.support.geo;

import com.chua.common.support.geo.geohash.GeoHash;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.text.DecimalFormat;

/**
 * 点位
 *
 * @author CH
 * @since 2022-05-10
 */
@Data
@Builder
@Accessors(fluent = true)
@AllArgsConstructor
public class Point {

    public static String DECIMAL_FORMAT_PATTERN = "0.0###############";
    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;


    /**
     * 两个点的距离(米)
     *
     * @param point 点
     * @return 距离
     */
    public double distance(Point point) {
        return Math.abs(GeoUtils.getDistanceOfMeter(this, point));
    }

    /**
     * geohash
     *
     * @return geohash
     */
    public String toGeoHash() {
        return GeoHash.encodeHash(this);
    }

    /**
     * geohash
     *
     * @param length 长度
     * @return geohash
     */
    public String toGeoHash(int length) {
        return GeoHash.encodeHash(this, length);
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", DECIMAL_FORMAT.format(longitude), DECIMAL_FORMAT.format(latitude));
    }
}
