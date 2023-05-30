package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.task.cache.JdkCacheable;
import com.chua.common.support.value.NullValue;
import com.chua.common.support.value.Value;

import java.util.concurrent.TimeUnit;

/**
 * @author CH
 */
@Spi("hanyu")
public class HanyuMockResolver implements MockResolver {

    protected static final Cacheable CACHEABLE = new JdkCacheable();

    protected static final String URL = "https://hanyu.baidu.com/s?wd=%s&from=poem";


    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String base = null;
        try {
            base = expressionParser.parseExpression(mock.base()).getValue(String.class);
        } catch (Exception e) {
            base = mock.base();
        }

        Value<Object> ifPresent = CACHEABLE.get(base);
        Document document = null;
        if (null != ifPresent) {
            document = ifPresent.getValue(Document.class);
        } else {
            try {
                HttpClientInvoker httpClientInvoker = HttpClient.get().url(String.format(URL, base)).newInvoker();
                HttpResponse execute = httpClientInvoker.execute();
                Value<Document> stringValue = Value.of(Jsoup.parse(execute.content(String.class)));
                CACHEABLE.put(base, stringValue);
                document = stringValue.getValue();
            } catch (Exception ignored) {
                CACHEABLE.put(base, NullValue.INSTANCE);
            }
        }

        if (null == document) {
            return null;
        }

        Elements elements = document.selectXpath("//div[@id='poem-detail-header']//div[@class='poem-detail-item-content']");
        return elements.text();
    }
}
