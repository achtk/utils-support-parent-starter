package com.chua.htmlunit.support.mock.resolver;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.mock.resolver.MockResolver;
import com.chua.common.support.spi.condition.PingCondition;
import com.chua.common.support.utils.RandomUtils;
import com.chua.common.support.value.NullValue;
import com.chua.common.support.value.Value;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.value.NullValue.INSTANCE;

/**
 * url like
 * @author CH
 */
@SpiCondition(onCondition = PingCondition.class)
@Spi("url_like")
public class UrlLikeMockResolver implements MockResolver {
    private static final Map<String, Value<List<HtmlElement>>> CACHE = new ConcurrentReferenceHashMap<>(512);

    private static final String URL = "https://st.so.com";
    static final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    private static HtmlPage HTML_PAGE;

    static {
        webClient.getOptions().setCssEnabled(false);//关闭css
        webClient.getOptions().setJavaScriptEnabled(true);//开启js
        webClient.getOptions().setRedirectEnabled(true);//重定向
        webClient.getOptions().setThrowExceptionOnScriptError(false);//关闭js报错
        webClient.getOptions().setTimeout(50000);//超时时间
        webClient.getCookieManager().setCookiesEnabled(true);//允许cookie
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
        try {
            HTML_PAGE = webClient.getPage(URL);
        } catch (IOException ignored) {
        }
    }


    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String base = expressionParser.parseExpression(mock.base()).getValue(String.class);
        try {
            Value<List<HtmlElement>> byXPath = CACHE.computeIfAbsent(base, s -> {
                DomElement elementById = HTML_PAGE.getElementById("stInput");
                elementById.setNodeValue(base);
                elementById.setAttribute("value", base);
                List<DomElement> input = HTML_PAGE.getElementsByTagName("input");
                for (DomElement domElement : input) {
                    if ("搜索".equals(domElement.getAttribute("value"))) {
                        try {
                            return Value.of(analysis(domElement.click()));
                        } catch (IOException ignored) {
                        }
                    }
                }
                return INSTANCE;
            });

            if(byXPath.isNull()) {
                return null;
            }
            List<HtmlElement> value = byXPath.getValue();
            return value.get(RandomUtils.randomInt(0, value.size())).getAttribute("src");

        } catch (Exception ignored) {
        }
        return null;
    }

    private List<HtmlElement> analysis(Page click) {
        HtmlPage htmlPage = (HtmlPage) click;
        DomElement similarity = htmlPage.getElementById("similarity");
        DomNodeList<HtmlElement> ul = similarity.getElementsByTagName("ul");
        int size = ul.size();
        HtmlElement htmlElement = ul.get(RandomUtils.randomInt(0, size));
        return htmlElement.getByXPath("//img");
    }
}
