package com.chua.example.geo;

import com.chua.common.support.geo.*;
import com.chua.common.support.spi.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import static com.chua.common.support.geo.transform.Coordinate.GCJ02;
import static com.chua.common.support.geo.transform.Coordinate.WGS84;


/**
 * @author CH
 */
@Slf4j
public class GeoExample {

    public static void main(String[] args) {
        ipPosition();
        conversion();
        reverseGeoPosition();


    }

    /**
     * 地址解析
     */
    private static void ipPosition() {
        IpPosition ipPosition = IpBuilder.newBuilder().database("Z://").build("geo");
        GeoCity city = ipPosition.getCity("112.124.44.21");
        System.out.println(city);
    }

    /**
     * 逆地址
     */
    private static void reverseGeoPosition() {
        Point source = Point.builder().longitude(106.706977).latitude(29.891160).build();
        ReverseGeoPosition reverseGeoPosition = ServiceProvider.of(ReverseGeoPosition.class).getExtension("names");
        GeoCity geoCity = reverseGeoPosition.nearestPlace(source.latitude(), source.longitude());
        System.out.println(geoCity);
    }

    /**
     * 经纬度转化
     */
    public static void conversion() {
        Point source = Point.builder().longitude(106.706977).latitude(29.891160).build();
        Point point = WGS84.converterTo(GCJ02).transform(source);
        log.info("{} -> {}", source, point.toString());
    }
}
