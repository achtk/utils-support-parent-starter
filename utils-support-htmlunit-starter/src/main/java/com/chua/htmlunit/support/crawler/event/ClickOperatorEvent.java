package com.chua.htmlunit.support.crawler.event;

import com.chua.common.support.crawler.event.Event;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 页面事件
 */
public class ClickOperatorEvent implements Event<HtmlPage, HtmlPage> {

    private String clickBtnId;
    private Consumer<HtmlPage> consumer;

    public ClickOperatorEvent(String clickBtnId, Consumer<HtmlPage> consumer) {
        this.clickBtnId = clickBtnId;
        this.consumer = consumer;
    }

    @Override
    public HtmlPage filter(HtmlPage input) {
        consumer.accept(input);
        DomElement elementById = input.getElementById(clickBtnId);
        try {
            return elementById.click();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
