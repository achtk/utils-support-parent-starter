package com.chua.common.support.crawler.page;

import com.chua.common.support.crawler.CrawlerBuilder;
import lombok.NoArgsConstructor;

/**
 * 页面加载器
 * @author CH
 */
@NoArgsConstructor
public abstract class AbstractPageLoader implements PageLoader {

    protected CrawlerBuilder config;

    @Override
    public void configure(CrawlerBuilder crawlerBuilder) {
        this.config = crawlerBuilder;
    }
}
