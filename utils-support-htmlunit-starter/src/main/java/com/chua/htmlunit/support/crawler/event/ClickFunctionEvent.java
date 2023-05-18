package com.chua.htmlunit.support.crawler.event;

import com.chua.common.support.crawler.event.Event;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.function.Function;

/**
 * 页面事件
 */
public class ClickFunctionEvent implements Event<HtmlPage, Page> {

    private Function<HtmlPage, Page> function;

    public ClickFunctionEvent(Function<HtmlPage, Page> function) {
        this.function = function;
    }

    @Override
    public Page filter(HtmlPage input) {
        return function.apply(input);
    }
}
