package com.chua.common.support.crawler.node.impl;

import com.chua.common.support.crawler.entity.JsParser;
import com.chua.common.support.crawler.node.AbstractPageParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.page.PageLoader;
import lombok.Getter;
import lombok.Setter;

/**
 * js解析
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/20
 */
@Getter
@Setter
public class PageJsParser extends AbstractPageParser<JsParser> {


    @Override
    public Parser newParser(PageLoader pageLoader) {
        return new PageHrefParser();
    }
}
