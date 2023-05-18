package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.lang.pinyin.PinyinFactory;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.StringUtils;

/**
 * 姓名拼音
 *
 * @author CH
 */
@Spi("name_pinyin")
public class NamePinyinMockResolver implements MockResolver {

    private static final NameMockResolver RESOLVER = new NameMockResolver();

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(mock, expressionParser);
    }

    /**
     * 随机纬度(中国)
     *
     * @param mock             基础数据
     * @param expressionParser expressionParser
     * @return 随机纬度
     */
    public static String getRandom(MockValue mock, ExpressionParser expressionParser) {
        PinyinFactory pinyinFactory = ServiceProvider.of(PinyinFactory.class).getExtension("pinyin");
        String base = expressionParser.parseExpression(mock.base()).getValue(String.class);
        if(!StringUtils.isNullOrEmpty(base)) {
            return pinyinFactory.transferSplit(base);
        }
        return pinyinFactory.transferSplit(RESOLVER.resolve(mock, expressionParser));
    }
}
