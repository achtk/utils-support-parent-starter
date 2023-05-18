package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;

/**
 * mock
 *
 * @author CH
 */
public interface MockResolver {
    /**
     * 获取数据
     *
     * @param mock             基础信息
     * @param expressionParser expressionParser
     * @return 数据
     */
    String resolve(MockValue mock, ExpressionParser expressionParser);
}
