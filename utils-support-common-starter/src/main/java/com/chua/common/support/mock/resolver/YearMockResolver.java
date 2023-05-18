package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.StringUtils;

/**
 * 年
 *
 * @author CH
 */
@Spi("year")
public class YearMockResolver extends DateMockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String format = StringUtils.defaultString(mock.formatter(), "yyyy");
        return getValue(mock, format);
    }
}
