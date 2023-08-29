package com.chua.common.support.net;

import com.chua.common.support.geo.GeoCity;
import com.chua.common.support.geo.IpBuilder;
import com.chua.common.support.geo.IpPosition;
import com.chua.common.support.utils.Preconditions;

/**
 * ip地址
 *
 * @author CH
 */
public class IpAddress {

    private final String ip;
    private final long ips;
    private IpPosition ipPosition;

    public IpAddress(String ip, IpPosition ipPosition) {
        Preconditions.checkNotNull(IpUtils.isMatch(ip), "当前地址非IPV4地址");
        this.ip = ip;
        this.ips = IpUtils.ip2Long(ip);
        this.ipPosition = ipPosition;
    }

    /**
     * 初始化
     *
     * @param ip         ip
     * @param ipResolver 地址解析器
     */
    public IpAddress(String ip, String ipResolver) {
        this(ip, IpBuilder.newBuilder().build(ipResolver));
    }


    /**
     * ip获取城市
     *
     * @return 城市
     */
    public GeoCity getCity() {
        return null == ipPosition ? null : ipPosition.getCity(ip);
    }

    /**
     * ip获取国家
     *
     * @return 城市
     */
    public GeoCity getCountry() {
        return null == ipPosition ? null : ipPosition.getCountry(ip);
    }

    /**
     * ip获取国家
     *
     * @return 城市
     */
    public boolean in(String sourceIp, String targetIp) {
        if (!IpUtils.isMatch(sourceIp) || !IpUtils.isMatch(targetIp)) {
            throw new RuntimeException("请输入正确的IP地址");
        }

        return IpUtils.ip2Long(sourceIp) <= ips && IpUtils.ip2Long(targetIp) <= ips;
    }
}
