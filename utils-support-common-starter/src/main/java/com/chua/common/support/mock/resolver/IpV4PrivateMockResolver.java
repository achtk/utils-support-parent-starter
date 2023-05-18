package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;

/**
 * ipv4
 *
 * @author CH
 */
@Spi("private_ipv4")
public class IpV4PrivateMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 随机IP
     *
     * @return IP
     */
    public static String getRandom() {
        int x = RandomUtils.randomInt(1, 101);
        if (x % 2 == 0) {
            return "10." + RandomUtils.randomInt(0, 256) + "." + RandomUtils.randomInt(0, 256) + "." + RandomUtils.randomInt(0, 256);
        } else if (x % 3 == 0) {
            return "172." + RandomUtils.randomInt(16, 32) + "." + RandomUtils.randomInt(0, 256) + "." + RandomUtils.randomInt(0, 256);
        } else {
            return "192.168." + RandomUtils.randomInt(0, 256) + "." + RandomUtils.randomInt(0, 256);
        }
    }
}
