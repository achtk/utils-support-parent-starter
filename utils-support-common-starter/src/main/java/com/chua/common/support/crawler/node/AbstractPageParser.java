package com.chua.common.support.crawler.node;


import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;
import lombok.Data;

/**
 * 页面解析器
 *
 * @author CH
 * @version 1.0.0
 */
@Data
public abstract class AbstractPageParser<T> implements PageParser<T> {

    private Request pageRequest;
    private CrawlerBuilder crawlerBuilder;

    @Override
    public Parser newParser(PageLoader pageLoader) {
        return this;
    }

    @Override
    public String toString() {
        return "AbstractPageParser{" +
                "pageRequest=" + pageRequest +
                '}';
    }
}
