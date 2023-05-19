package com.chua.gps.support.example;


import com.chua.gps.support.provider.GeoLocation;

/**
 * @author CH
 * @version 1.0.0
 * @since 2021/7/1
 */
public class GeoLocationExample {

    public static void main(String[] args) {
        GeoLocation location = GeoLocation.builder().database("D:\\GeoLite2-City_20210629").build();
        System.out.println("218.75.14.163 -> " + location.city("218.75.14.163"));
    }
}
