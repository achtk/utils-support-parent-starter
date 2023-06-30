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
}
