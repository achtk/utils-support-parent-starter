package com.chua.example.geo;

import com.chua.common.support.geo.GeoCity;
import com.chua.common.support.geo.Point;
import com.chua.common.support.geo.ReverseGeoPosition;
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
        Point source = Point.builder().longitude(106.706977).latitude(29.891160).build();
        Point point = WGS84.converterTo(GCJ02).transform(source);
        log.info("{} -> {}", source, point.toString());

        ReverseGeoPosition reverseGeoPosition = ServiceProvider.of(ReverseGeoPosition.class).getExtension("names");
        GeoCity geoCity = reverseGeoPosition.nearestPlace(source.latitude(), source.longitude());
        System.out.println();

    }
}
