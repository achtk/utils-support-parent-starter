package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.CardUtils;
import com.chua.common.support.utils.RandomUtils;

/**
 * 性别
 *
 * @author CH
 */
@Spi("sex")
public class SexMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String base = expressionParser.parseExpression(mock.base()).getStringValue();
        if(CardUtils.isValidCard(base)) {
            Integer gender = CardUtils.getIdcardInfo(base).getGender();
            if (mock.returnType() == String.class) {
                return 0 == gender ? "女" : "男";
            }
            return String.valueOf(gender);
        }

        return getRandom(mock);
    }

    /**
     * 获取随机性别
     *
     * @return 性别
     */
    public static String getRandom(MockValue mock) {
        double aDouble = RandomUtils.randomDouble(-100, 100);
        if (mock.returnType() == String.class) {
            return aDouble > 0 ? "男" : "女";
        }
        return String.valueOf(aDouble > 0 ? 1 : 0);
    }
}
