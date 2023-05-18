package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;

/**
 * 教育程度
 *
 * @author CH
 */
@Spi("degree")
public class DegreeMockResolver implements MockResolver {
    private static final String[] DEGREE = new String[]{"小学", "初中", "中专/职业高中", "高中", "大学专科", "大学本科", "硕士", "博士"};

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }

    /**
     * 获取随机教育程度
     *
     * @return 教育程度
     */
    public static String getRandom() {
        return DEGREE[RandomUtils.randomInt(0, DEGREE.length)];
    }
}
