package com.chua.htmlunit.support.crawler.loader;

import com.chua.common.support.crawler.browser.BeanBrowser;
import com.chua.common.support.crawler.browser.Browser;
import com.chua.common.support.crawler.event.Event;
import com.chua.common.support.crawler.page.AbstractPageLoader;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * htmlunit
 *
 * @author CH
 */
@NoArgsConstructor
public class HtmlunitPageLoader extends AbstractPageLoader {
    // HtmlUnit 模拟浏览器
    final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);

    {
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);//关闭css
        webClient.getOptions().setJavaScriptEnabled(true);//开启js
        webClient.getOptions().setRedirectEnabled(true);//重定向
        webClient.getOptions().setThrowExceptionOnScriptError(false);//关闭js报错
        webClient.getOptions().setTimeout(50000);//超时时间
        webClient.getCookieManager().setCookiesEnabled(true);//允许cookie
        webClient.waitForBackgroundJavaScript(15000);
        webClient.waitForBackgroundJavaScriptStartingBefore(5000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
    }


    @Override
    public void close() throws Exception {
        webClient.close();
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

    @Override
    public Browser getBrowner() {
        return new BeanBrowser();
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
}
