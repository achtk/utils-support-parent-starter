package com.chua.common.support.geo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * geo-city
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@Accessors(fluent = true)
public class GeoCity {
    /**
     * 编码
     */
    private String isoCode;
    /**
     * 国家
     */
    private String country;
    /**
     * 省
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 邮编
     */
    private String postal;
    /**
     * 时区
     */
    private String timeZone;
    /**
     * 半径
     */
    private Integer radius;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 城市
     */
    private String subdivision;
    /**
     * isp
     */
    private String isp;
    /**
     * ip
     */
    private String ip;

    @Override
    public String toString() {
        return
                "编码:" + isoCode + '\n' +
                        "国家:" + country + '\n' +
                        "省份:" + province + '\n' +
                        "城市:" + city + '\n' +
                        "邮编:" + postal + '\n' +
                        "时区:" + timeZone + '\n' +
                        "半径:" + radius + '\n' +
                        "纬度:" + latitude + '\n' +
                        "经度:" + longitude + '\n' +
                        "城市:" + subdivision + '\n' +
                        "运营商:" + isp + '\n';

    }
}
