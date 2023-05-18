package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;

/**
 * 经度
 *
 * @author CH
 */
@Spi("longitude")
public class LongitudeMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 随机纬度(中国)
     *
     * @return 随机纬度
     */
    public static String getRandom() {
        return String.valueOf(RandomUtils.randomDouble(73.66D, 135.05D));
    }
}
