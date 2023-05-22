package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.utils.RandomUtils;

import java.util.List;

/**
 * ipv6
 *
 * @author CH
 */
@Spi("ipv6")
public class IpV6MockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 随机端口号
     * 注意: 不会生成1024及以下的端口号
     *
     * @return 随机端口号
     */
    public static String getRandom() {
        List<String> numbers = ImmutableBuilder.<String>builder().newArrayList();
        for (int i = 0; i < 8; i++) {
            numbers.add(Integer.toHexString(RandomUtils.randomInt(0, 65535)));
        }
        return Joiner.on(":").join(numbers);
    }
}
