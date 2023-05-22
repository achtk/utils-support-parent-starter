package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.RandomUtils;

import java.util.List;

/**
 * 社会信用代码
 *
 * @author CH
 */
@Spi("social_credit")
public class SocialCreditMockResolver implements MockResolver {

    /**
     * 统一社会信用代码候选字符(不使用I、O、Z、S、V)
     */
    private static final List<String> SOCIAL_CREDIT_CHARACTERS_LIST = ImmutableBuilder.<String>builder().add(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "T", "U", "W", "X", "Y").unmodifiableList();

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(mock, expressionParser);
    }

    /**
     * 随机统一社会信用代码
     *
     * @param mock             基础数据
     * @param expressionParser expressionParser
     * @return 随机统一社会信用代码
     */
    public static String getRandom(MockValue mock, ExpressionParser expressionParser) {
        String prefix = "91";
        //为避免与真实的社会信用代码重合，不计算校验码而是随机生成
        String checkCode = String.valueOf(RandomUtils.randomInt(0, 10));
        CityCodeMockResolver cityCodeMockResolver = new CityCodeMockResolver();
        String resolve = cityCodeMockResolver.resolve(mock, expressionParser);
        return prefix + resolve + Joiner.on("").join(CollectionUtils.getRandomElement(SOCIAL_CREDIT_CHARACTERS_LIST, 9)) + checkCode;
    }
}
