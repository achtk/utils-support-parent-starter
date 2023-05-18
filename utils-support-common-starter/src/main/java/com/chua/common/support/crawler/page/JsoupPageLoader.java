package com.chua.common.support.crawler.page;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.browser.BeanBrowser;
import com.chua.common.support.crawler.browser.Browser;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.nodes.Document;

/**
 * jsoup加载
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/21
 */
public class JsoupPageLoader implements PageLoader {
    private CrawlerBuilder crawlerBuilder;

    @Override
    public void close() throws Exception {

    }


    @Override
    public Document load(Request request) {
        return JsoupUtil.load(request);
    }

    @Override
    public void configure(CrawlerBuilder crawlerBuilder) {
        this.crawlerBuilder = crawlerBuilder;
    }

    @Override
    public Browser getBrowner() {
        return new BeanBrowser();
    }
}
