package com.chua.common.support.utils;

import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.range.IpRange;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 地址处理
 * @author CH
 */
public class IpUtils {
    /**
     * 目标地址是否匹配源地址
     * @param sourceAddress 源地址
     * @param targetAddress 目标地址
     * @return 目标地址是否匹配源地址
     */
    public static boolean isMatch(String sourceAddress, String targetAddress) {
        if(!sourceAddress.contains(SYMBOL_ASTERISK) && !sourceAddress.contains(SYMBOL_MINS)) {
            return sourceAddress.equals(targetAddress);
        }
        if(sourceAddress.contains(SYMBOL_ASTERISK) && !sourceAddress.contains(SYMBOL_MINS)) {
            return PathMatcher.INSTANCE.match(sourceAddress, targetAddress);
        }

        if(!sourceAddress.contains(SYMBOL_ASTERISK) && sourceAddress.equals(SYMBOL_MINS)) {
            String[] split = sourceAddress.split(SYMBOL_COMMA, 2);
            if(split.length != 2) {
                return false;
            }
            String start = split[0];
            String end = split[1];
            IpRange ipRange = new IpRange(start, end);
            return ipRange.inRange(targetAddress);
        }

        return false;
    }


    /**
     * 把字符串IP转换成long
     *
     * @param ipStr 字符串IP
     * @return IP对应的long值
     */
    public static long ip2Long(String ipStr) {
        String[] ip = ipStr.split("\\.");
        return (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16)
                + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
    }

    /**
     * 把IP的long值转换成字符串
     *
     * @param ipLong IP的long值
     * @return long值对应的字符串
     */
    public static String long2Ip(long ipLong) {
        StringBuilder ip = new StringBuilder();
        ip.append(ipLong >>> 24).append(".");
        ip.append((ipLong >>> 16) & 0xFF).append(".");
        ip.append((ipLong >>> 8) & 0xFF).append(".");
        ip.append(ipLong & 0xFF);
        return ip.toString();
    }

    /**
     * 校验IP是否合法
     *
     * @param ip ip
     * @return 结果
     */
    public static boolean isMatch(String ip) {
        return StringUtils.isNotEmpty(ip) && ip.matches(IPV4);
    }
}
