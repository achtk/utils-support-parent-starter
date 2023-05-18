package com.chua.common.support.mock.resolver;

import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * port
 *
 * @author CH
 */
@Spi("port")
public class PortMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(StringUtils.defaultString(mock.base(), ":"));
    }

    /**
     * 随机MAC地址
     *
     * @param splitter 分隔符
     * @return 随机MAC地址
     */
    public static String getRandom(String splitter) {
        int count = 6;
        List<String> mac = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int n = RandomUtils.randomInt(0, 255);
            mac.add(String.format("%02x", n));
        }
        return Joiner.on(!StringUtils.isEmpty(splitter) ? splitter : "-").join(mac).toUpperCase();
    }
}
