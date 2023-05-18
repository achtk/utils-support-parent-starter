package com.chua.common.support.crawler.url;

import com.chua.common.support.crawler.CrawlerBuilder;
import lombok.NoArgsConstructor;

/**
 * url加载器
 * @author CH
 */
@NoArgsConstructor
public abstract class AbstractUrlLoader implements UrlLoader{

    protected CrawlerBuilder crawlerBuilder;

    public AbstractUrlLoader(CrawlerBuilder crawlerBuilder) {
        this.crawlerBuilder = crawlerBuilder;
    }
}
