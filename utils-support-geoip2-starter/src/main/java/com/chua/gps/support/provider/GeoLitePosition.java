package com.chua.gps.support.provider;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.geo.GeoCity;
import com.chua.common.support.geo.IpPosition;
import com.chua.common.support.io.CompressInputStream;
import com.chua.common.support.lang.profile.DelegateProfile;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.utils.IoUtils;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Locale;

/**
 * geo定位
 *
 * @author CH
 * @since 2022-05-09
 */
@Spi("geo")
public class GeoLitePosition extends DelegateProfile implements IpPosition {

    private static final Locale LOCALE = Locale.getDefault();
    private static final String LANGUAGE_TAG = LOCALE.toLanguageTag();
    /**
     * 数据库加载器
     */
    private DatabaseReader databaseReader;

    @Override
    public void afterPropertiesSet() {
        Object database1 = getString("database");
        if (null == database1) {
            this.databaseReader = initialClasspath();
            return;
        }
        try {
            if (database1 instanceof File) {
                this.databaseReader = new DatabaseReader.Builder((File) database1).build();
            } else if (database1 instanceof String) {
                File file = new File((String) database1);
                if (file.exists()) {
                    this.databaseReader = new DatabaseReader.Builder(file).build();
                } else {
                    this.databaseReader = initialClasspath();
                }
            } else if (database1 instanceof InputStream) {
                this.databaseReader = new DatabaseReader.Builder((InputStream) database1).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化
     *
     * @return DatabaseReader
     */
    private DatabaseReader initialClasspath() {
        try {
            InputStream resourceAsStream = GeoLitePosition.class.getResourceAsStream("GeoLite2-City.mmdb");
            if (null != resourceAsStream) {
                this.databaseReader = new DatabaseReader.Builder(resourceAsStream).build();
            }
        } catch (Throwable ignored) {
        }

        try {
            this.databaseReader = new DatabaseReader.Builder(new CompressInputStream(ResourceProvider.of("classpath*:**/GeoLite2-City.tar.gz").getResource().getUrl(), "GeoLite2-City.mmdb")).build();
        } catch (Throwable ignored1) {
        }

        return databaseReader;
    }

    @Override
    public GeoCity getCity(String ip) {
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

        return geoCity
                .isoCode(country.getIsoCode())
                .country(country.getNames().get(LANGUAGE_TAG))
                .subdivision(subdivision.getNames().get(LANGUAGE_TAG))
                .city(city.getNames().get(LANGUAGE_TAG))
                .postal(postal.getCode())
                .timeZone(location.getTimeZone())
                .radius(location.getAccuracyRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude());
    }

    @Override
    public GeoCity getCountry(String ip) {
        GeoCity geoCity = new GeoCity();
        CountryResponse response = null;
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            // Replace "city" with the appropriate method for your database, e.g.,
            // "country".
            response = databaseReader.country(ipAddress);
        } catch (Exception ignore) {
            return geoCity;
        }
        Country country = response.getCountry();

        return geoCity.isoCode(country.getIsoCode())
                .country(country.getName());
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(databaseReader);
    }
}
