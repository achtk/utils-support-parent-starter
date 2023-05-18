package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;

import java.util.UUID;

/**
 * uuid
 *
 * @author CH
 */
@Spi("uuid")
public class UuidMockResolver implements MockResolver {

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
        return UUID.randomUUID().toString();
    }
}
