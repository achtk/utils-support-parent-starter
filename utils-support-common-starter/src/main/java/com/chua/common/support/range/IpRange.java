package com.chua.common.support.range;

import lombok.Data;

import java.io.Serializable;

/**
 * ip范围
 *
 * @author CH
 */
@Data
public class IpRange implements Serializable{

    private String beginIp;

    private long beginIpNum;

    private String endIp;

    private long endIpNum;

    public IpRange(String beginIp, String endIp) {
        this.beginIp = beginIp;
        this.beginIpNum = ipv4ToLong(beginIp);
        this.endIp = endIp;
        this.endIpNum = ipv4ToLong(endIp);
    }

    public IpRange(long beginIpNum, long endIpNum) {
        this.beginIp = longToIpv4(beginIpNum);
        this.beginIpNum = beginIpNum;
        this.endIp = longToIpv4(endIpNum);
        this.endIpNum = endIpNum;
    }

    /**
     * 整数转IPv4地址
     *
     * @param ipv4Num 整数
     * @return IPv4地址
     */
    public static String longToIpv4(long ipv4Num) {
        StringBuilder result = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            result.insert(0, (ipv4Num & 0xff));
            if (i < 3) {
                result.insert(0, '.');
            }
            ipv4Num = ipv4Num >> 8;
        }

        return result.toString();
    }

    /**
     * IPv4地址转整数
     *
     * @param ipv4 IPv4地址
     * @return 整数
     */
    public static long ipv4ToLong(String ipv4) {
        String[] part = ipv4.split("\\.");
        long num = 0;
        for (int i = 0; i < part.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(part[i]) % 256 * Math.pow(256, power)));
        }
        return num;
    }

    public boolean inRange(String value) {
        long ipv4ToLong = ipv4ToLong(value);
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
