package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;

/**
 * 端口号
 *
 * @author CH
 */
@Spi("port")
public class MacMockResolver implements MockResolver {

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
        return String.valueOf(RandomUtils.randomInt(1025, 65535));
    }
}
