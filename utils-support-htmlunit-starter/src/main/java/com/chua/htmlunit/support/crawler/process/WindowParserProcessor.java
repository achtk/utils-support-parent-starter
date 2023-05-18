package com.chua.htmlunit.support.crawler.process;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.node.BrowerApiParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.process.ParserProcessor;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.SimpleTypeValue;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 页面解析器
 *
 * @author CH
 * @version 1.0.0
 */
@SuppressWarnings("ALL")
@Slf4j
public class WindowParserProcessor implements ParserProcessor {

    private BrowerApiParser pageParser;
    private Request pageRequest;
    private Document html;
    private Class<?> classType;
    private CrawlerBuilder config;


    @Override
    public boolean matcher(Parser parser) {
        if (parser instanceof BrowerApiParser) {
            this.pageParser = (BrowerApiParser) parser;
            this.pageRequest = parser.getPageRequest();
            this.config = parser.getCrawlerBuilder();
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public boolean processor(Parser parser, UrlLoader urlLoader) {
        WebClient webClient = pageParser.getBrowner().get(WebClient.class);
        String url = pageRequest.getUrl();
        WebRequest webRequest = new WebRequest(new URL(url), config.ifPost() ? HttpMethod.POST : HttpMethod.GET);
        webRequest.setCharset(StandardCharsets.UTF_8);
        Map<String, Object> param = pageRequest.getParam();
        if(null != param) {
            List<NameValuePair> nameValuePairs = new LinkedList<>();
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                nameValuePairs.add(new NameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            webRequest.setRequestParameters(nameValuePairs);
        }
        ClassUtils.forFilterType(config.event(), new SimpleTypeValue(webClient, webRequest), "filter", 0);
//        webRequest.setRequestParameters(Lists.newArrayList(new NameValuePair("page", "1"), new NameValuePair("rows", "20")));
        Page page = null;
        try {
            page = webClient.getPage(webRequest);
        } catch (Throwable e) {
            urlLoader.addUrl(pageRequest.getOriginal());
            return false;
        }
        String string = page.getWebResponse().getContentAsString();
        JsoupUtil.output(url, pageParser, string);
        return true;
    }

}
