package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.Mock;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.CardUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.RandomUtils;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_BRACKETS;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET;

/**
 * age
 *
 * @author CH
 */
@Spi("age")
public class AgeMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(mock, expressionParser);
    }

    /**
     * 获取随机城市
     *
     * @param mock             mock
     * @param expressionParser expressionParser
     * @return 城市
     */
    public static String getRandom(MockValue mock, ExpressionParser expressionParser) {
        String base = expressionParser.parseExpression(mock.base()).getStringValue();
        if(CardUtils.isValidCard(base)) {
            return String.valueOf(CardUtils.getAgeByIdCard(base));
        }


        if (base.startsWith(SYMBOL_LEFT_BRACKETS) || base.startsWith(SYMBOL_LEFT_SQUARE_BRACKET)) {
            Range<Double> range = Range.of(base);
            return String.valueOf(NumberUtils.parseNumber(range.random(), Integer.class));
        }

        if(base.length() > 3) {
            base = mock.symbol() == Mock.Symbol.AFTER ? "0" : "100";
        }

        String baseValue = StringUtils.defaultString(base, "0");
        if (mock.symbol() == Mock.Symbol.AFTER) {
            return String.valueOf(RandomUtils.randomInt(NumberUtils.toInt(baseValue, 0), 100));
        }
        return String.valueOf(RandomUtils.randomInt(0, NumberUtils.toInt(baseValue, 0)));
    }
}
