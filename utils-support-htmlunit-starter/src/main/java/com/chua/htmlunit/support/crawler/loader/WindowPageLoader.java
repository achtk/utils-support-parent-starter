package com.chua.htmlunit.support.crawler.loader;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.browser.Browser;
import com.chua.common.support.crawler.event.Event;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.SimpleTypeValue;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * htmlunit
 * 模拟单个浏览器可以使用缓存
 * @author CH
 */
@NoArgsConstructor
public class WindowPageLoader extends HtmlunitPageLoader {

    @Getter
    private Browser browser;
    private WindowLoader windowLoader;

    public WindowPageLoader(WindowLoader windowLoader) {
        this.windowLoader = windowLoader;
    }

    @Override
    public void close() throws Exception {
        windowLoader.close();
    }

    @SuppressWarnings("ALL")
    @Override
    public void configure(CrawlerBuilder crawlerBuilder) {
        super.configure(crawlerBuilder);
        browser = windowLoader.createWindow();
        HtmlPage htmlPage = browser.get(HtmlPage.class);
        ClassUtils.forFilterType(crawlerBuilder.preEvent(), new SimpleTypeValue(htmlPage), "filter", 0);
    }

    @Override
    public Document load(Request request) {
        //cookie
        try {
            HtmlPage page = webClient.getPage(request.getUrl());
            page = eventStream(request.getEvent(), page);
            String pageAsXml = page.asXml();
            // Jsoup解析处理
            return Jsoup.parse(pageAsXml, request.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 页面事件流
     *
     * @param event 事件
     * @param page  页面
     * @return 页面
     */
    @SuppressWarnings("ALL")
    private HtmlPage eventStream(List<Event<?, ?>> event, HtmlPage page) {
        Page htmlPage = page;
        for (Event event1 : event) {
            htmlPage = (Page) event1.filter(htmlPage);
        }
        return (HtmlPage) htmlPage;
    }

    @Override
    public Browser getBrowner() {
        return browser;
    }
}
