package com.chua.common.support.crawler.node;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;
import lombok.Data;

/**
 * api解析
 *
 * @author CH
 * @version 1.0.0
 */
@Data
public class JsonApiParser implements ApiParser {
    private Request pageRequest;
    private CrawlerBuilder crawlerBuilder;

    @Override
    public void parse(String url, String pageSource) {
        System.out.println();
    }

    @Override
    public Parser newParser(PageLoader pageLoader) {
        return new JsonApiParser();
    }
}
