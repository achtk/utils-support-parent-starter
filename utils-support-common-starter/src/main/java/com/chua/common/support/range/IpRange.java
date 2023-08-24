package com.chua.common.support.range;

import lombok.Data;

import java.io.Serializable;

import static com.chua.common.support.net.IpUtils.ip2Long;
import static com.chua.common.support.net.IpUtils.long2Ip;

/**
 * ip范围
 *
 * @author CH
 */
@Data
public class IpRange implements Serializable {

    private String beginIp;

    private long beginIpNum;

    private String endIp;

    private long endIpNum;

    public IpRange(String beginIp, String endIp) {
        this.beginIp = beginIp;
        this.beginIpNum = ip2Long(beginIp);
        this.endIp = endIp;
        this.endIpNum = ip2Long(endIp);
    }

    public IpRange(long beginIpNum, long endIpNum) {
        this.beginIp = long2Ip(beginIpNum);
        this.beginIpNum = beginIpNum;
        this.endIp = long2Ip(endIpNum);
        this.endIpNum = endIpNum;
    }

    public boolean inRange(String value) {
        long ipv4ToLong = ip2Long(value);
        return beginIpNum <= ipv4ToLong && ipv4ToLong >= endIpNum;
    }

    public boolean inRangeAll(Iterable<? extends String> value) {
        for (String s : value) {
            if(!inRange(s)) {
                return false;
            }
        }
        return true;
    }

}
