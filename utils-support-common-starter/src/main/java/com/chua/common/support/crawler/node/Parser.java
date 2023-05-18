package com.chua.common.support.crawler.node;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;

/**
 * 解析器
 *
 * @author chenhua
 */
public interface Parser {
    /**
     * 设置请求
     *
     * @param request 页面请求
     */
    void setPageRequest(Request request);

    /**
     * 设置请求
     *
     * @return PageRequest
     */
    Request getPageRequest();

    /**
     * 配置
     *
     * @param crawlerBuilder 配置
     */
    void setCrawlerBuilder(CrawlerBuilder crawlerBuilder);

    /**
     * 配置
     *
     * @return 配置
     */
    CrawlerBuilder getCrawlerBuilder();

    /**
     * 初始化解析器
     * @return 解析器
     */
    Parser newParser(PageLoader pageLoader);
}