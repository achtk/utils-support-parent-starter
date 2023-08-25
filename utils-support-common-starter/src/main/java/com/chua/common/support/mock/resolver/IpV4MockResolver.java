package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.net.IpUtils;
import com.chua.common.support.range.IpRange;
import com.chua.common.support.utils.RandomUtils;

import java.util.List;

/**
 * ipv4
 *
 * @author CH
 */
@Spi("ipv4")
public class IpV4MockResolver implements MockResolver {

    private static final String[] CN_IP = new String[]{"1.0.1.0,1.0.3.255", "1.0.8.0,1.0.15.255", "1.0.32.0,1.0.63.255", "1.1.0.0,1.1.0.255", "1.1.2.0,1.1.63.255", "1.2.0.0,1.2.2.255", "1.2.4.0,1.2.127.255", "1.3.0.0,1.3.255.255", "1.4.1.0,1.4.127.255", "1.8.0.0,1.8.255.255", "1.10.0.0,1.10.9.255", "1.10.11.0,1.10.127.255", "1.12.0.0,1.15.255.255", "1.24.0.0,1.31.255.255", "1.45.0.0,1.45.255.255", "1.48.0.0,1.51.255.255", "1.56.0.0,1.63.255.255", "1.68.0.0,1.71.255.255", "1.80.0.0,1.95.255.255", "1.116.0.0,1.119.255.255", "1.180.0.0,1.185.255.255", "1.188.0.0,1.199.255.255", "1.202.0.0,1.207.255.255", "14.0.0.0,14.0.7.255", "14.0.12.0,14.0.15.255", "14.1.0.0,14.1.3.255", "14.1.24.0,14.1.27.255", "14.1.96.0,14.1.99.255", "14.1.108.0,14.1.111.255", "14.16.0.0,14.31.255.255", "14.102.128.0,14.102.131.255", "14.102.156.0,14.102.159.255", "14.102.180.0,14.102.183.255", "14.103.0.0,14.127.255.255", "14.130.0.0,14.131.255.255", "14.134.0.0,14.135.255.255", "14.144.0.0,14.159.255.255", "14.192.60.0,14.192.63.255", "14.192.76.0,14.192.79.255", "14.196.0.0,14.197.255.255", "14.204.0.0,14.205.255.255", "14.208.0.0,14.223.255.255", "27.0.128.0,27.0.135.255", "27.0.160.0,27.0.167.255", "27.0.188.0,27.0.191.255", "27.0.204.0,27.0.215.255", "27.8.0.0,27.31.255.255", "27.34.232.0,27.34.239.255", "27.36.0.0,27.47.255.255", "27.50.40.0,27.50.47.255", "27.50.128.0,27.50.255.255", "27.54.72.0,27.54.79.255", "27.54.152.0,27.54.159.255", "27.54.192.0,27.54.255.255", "27.98.208.0,27.98.255.255", "27.99.128.0,27.99.255.255", "27.103.0.0,27.103.255.255", "27.106.128.0,27.106.191.255", "27.106.204.0,27.106.207.255", "27.109.32.0,27.109.63.255", "27.109.124.0,27.109.127.255", "27.112.0.0,27.112.63.255", "27.112.80.0,27.112.95.255", "27.112.112.0,27.112.119.255", "27.113.128.0,27.113.191.255", "27.115.0.0,27.115.127.255", "27.116.44.0,27.116.47.255"};
    private static final List<IpRange> IP_RANGES = ImmutableBuilder.<IpRange>builder().newArrayList();


    static {
        for (String s : CN_IP) {
            List<String> row = Splitter.on(",").splitToList(s);
            IpRange range = new IpRange(row.get(0), row.get(1));
            IP_RANGES.add(range);
        }
    }

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 获取ipv4
     * @return ipv4
     */
    public static String getRandom() {
        IpRange range = RandomUtils.getRandomElement(IP_RANGES);
        if (range == null) {
            return null;
        }
        long ipv4Num = RandomUtils.randomLong(range.getBeginIpNum(), range.getEndIpNum());
        return IpUtils.long2Ip(ipv4Num);
    }
}
