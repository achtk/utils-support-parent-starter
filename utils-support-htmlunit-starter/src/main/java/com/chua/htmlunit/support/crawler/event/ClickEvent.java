package com.chua.htmlunit.support.crawler.event;

import com.chua.common.support.crawler.event.Event;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

/**
 * 页面事件
 */
public class ClickEvent implements Event<HtmlPage, HtmlPage> {

    private String clickBtnId;

    public ClickEvent(String clickBtnId) {
        this.clickBtnId = clickBtnId;
    }

    @Override
    public HtmlPage filter(HtmlPage input) {
        DomElement elementById = input.getElementById(clickBtnId);
        try {
            return elementById.click();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
