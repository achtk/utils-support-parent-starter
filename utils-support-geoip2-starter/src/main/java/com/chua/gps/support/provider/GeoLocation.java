package com.chua.gps.support.provider;

import com.chua.common.support.geo.GeoCity;
import com.chua.common.support.resource.ResourceProvider;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;

/**
 * 地理位置
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/7/1
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeoLocation {
    /**
     * 数据库
     */
    private InputStream database;
    /**
     * 数据库加载器
     */
    private DatabaseReader databaseReader;

    /**
     * ip获取城市
     *
     * @param ip ip
     * @return 城市
     */
    public GeoCity city(String ip) {
        GeoCity geoCity = new GeoCity();
        CityResponse response = null;
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            // Replace "city" with the appropriate method for your database, e.g.,
            // "country".
            response = databaseReader.city(ipAddress);
        } catch (Exception ignore) {
            return geoCity;
        }
        Subdivision subdivision = response.getMostSpecificSubdivision();
        Country country = response.getCountry();
        City city = response.getCity();
        Postal postal = response.getPostal();
        Location location = response.getLocation();

        return geoCity.isoCode(country.getIsoCode())
                .country(country.getName())
                .subdivision(subdivision.getName())
                .city(city.getName())
                .postal(postal.getCode())
                .timeZone(location.getTimeZone())
                .radius(location.getAccuracyRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude());
    }

    /**
     * 构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 初始化
     */
    private void initial() throws IOException {
        // This creates the DatabaseReader object. To improve performance, reuse
        // the object across lookups. The object is thread-safe.
        this.databaseReader = new DatabaseReader.Builder(database).build();
    }

    /**
     * 构建器
     */
    public static class Builder {

        GeoLocation geoLocation = new GeoLocation();

        /**
         * 初始化
         *
         * @return 初始化
         */
        public GeoLocation build() {
            try {
                geoLocation.initial();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return geoLocation;
        }

        /**
         * 数据库
         *
         * @param s 数据库
         * @return this
         */
        public Builder database(String s) {
            try {
                geoLocation.database = ResourceProvider.of(s).getResource().openStream();
            } catch (IOException ignored) {
            }
            return this;
        }

        /**
         * 数据库
         *
         * @param inputStream 数据库
         * @return this
         */
        public Builder database(InputStream inputStream) {
            geoLocation.database = inputStream;
            return this;
        }

        /**
         * 数据库
         *
         * @param file 数据库
         * @return this
         */
        public Builder database(File file) {
            if (null != file) {
                if (file.isDirectory()) {
                    file = new File(file.getAbsolutePath(), "GeoLite2-City.mmdb");
                }
                try {
                    geoLocation.database = Files.newInputStream(file.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return this;
        }
    }
}
