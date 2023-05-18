package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.RandomUtils;
import com.chua.common.support.utils.StringUtils;

import java.awt.*;

/**
 * color
 *
 * @author CH
 */
@Spi("color")
public class ColorMockResolver implements MockResolver {

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(StringUtils.defaultString(mock.base(), ""));
    }

    /**
     * 随机MAC地址
     *
     * @param splitter 分隔符
     * @return 随机MAC地址
     */
    public static String getRandom(String splitter) {
        Color color = new Color(
                RandomUtils.randomInt(0, 255),
                RandomUtils.randomInt(0, 255),
                RandomUtils.randomInt(0, 255)
        );

        return String.valueOf(color.getRGB());
    }
}
