package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;

import static com.chua.common.support.utils.NumberUtils.getNum;

/**
 * 手机号
 *
 * @author CH
 */
@Spi("phone")
public class PhoneMockResolver implements MockResolver {

    private static final String[] TEL_FIRST = new String[]{"134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "130", "131", "132", "155", "156", "133", "153"};

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 获取随机手机号
     *
     * @return 手机号
     */
    public static String getRandom() {
        int index = getNum(0, TEL_FIRST.length - 1);
        String first = TEL_FIRST[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }
}
