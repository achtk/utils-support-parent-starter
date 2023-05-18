package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.utils.CardUtils;
import com.chua.common.support.utils.NumberUtils;

import java.time.LocalDate;

/**
 * 出生日期
 *
 * @author CH
 */
@Spi("birthday")
public class BirthdayMockResolver extends DateMockResolver {


    private static final int YEAR = 40;

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String base = expressionParser.parseExpression(mock.base()).getValue(String.class);
        if(CardUtils.isValidCard(base)) {
            return String.valueOf(CardUtils.getBirthByIdCard(base));
        }

        return getRandom(NumberUtils.toInt(mock.base(), -1));
    }

    /**
     * 随机出生日期
     * @param baseYear 年
     * @return 出生日期
     */
    public static String getRandom(int baseYear) {
        if(baseYear < -1) {
            baseYear = YEAR;
        }

        return DateUtils.format(randomPastDate(LocalDate.now().minusYears(baseYear), 365 * baseYear), "yyyy-MM-dd");
    }
}
