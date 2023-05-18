package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.value.Value;

/**
 * 翻译
 *
 * @author CH
 */
@Spi("hanyu_yiwen")
public class HanyuTranslateMockResolver extends HanyuMockResolver {


    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String base = null;
        try {
            base = expressionParser.compile(mock.base()).getStringValue();
        } catch (Exception e) {
            base = mock.base();
        }

        super.resolve(mock, expressionParser);

        Value<Document> ifPresent = CACHE.getIfPresent(base);
        if (null == ifPresent || ifPresent.isNullValue()) {
            return null;
        }

        Document value = ifPresent.getValue();
        return value.selectXpath("//div[@id='means_zhushi_div']/div[@id='poem-detail-translation']/div[3]").text();
    }
}
