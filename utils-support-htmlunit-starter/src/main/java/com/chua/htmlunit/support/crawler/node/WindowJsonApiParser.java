package com.chua.htmlunit.support.crawler.node;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.browser.Browser;
import com.chua.common.support.crawler.node.BrowerApiParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * browner
 */
@Data
@NoArgsConstructor
public class WindowJsonApiParser implements BrowerApiParser {

    private Request pageRequest;
    private CrawlerBuilder crawlerBuilder;
    private PageLoader pageLoader;

    public WindowJsonApiParser(PageLoader pageLoader) {
        this.pageLoader = pageLoader;
    }

    @Override
    public void parse(String url, String pageSource) {

    }

    @Override
    public Browser getBrowner() {
        return pageLoader.getBrowner();
    }


    @Override
    public Parser newParser(PageLoader pageLoader) {
        return new WindowJsonApiParser(pageLoader);
    }
}
