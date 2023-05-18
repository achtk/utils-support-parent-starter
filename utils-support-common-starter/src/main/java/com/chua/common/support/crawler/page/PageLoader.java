package com.chua.common.support.crawler.page;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.browser.Browser;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.nodes.Document;

/**
 * 页面加载器
 * @author CH
 */
public interface PageLoader extends AutoCloseable {

    /**
     * load page
     *
     * @param request 请求
     * @return Document
     */
    Document load(Request request);

    /**
     * 初始化
     * @param crawlerBuilder 配置
     */
    void configure(CrawlerBuilder crawlerBuilder);

    /**
     * 浏览器
     * @return 浏览器
     */
    Browser getBrowner();
}
