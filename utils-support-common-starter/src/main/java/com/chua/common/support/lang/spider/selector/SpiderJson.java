package com.chua.common.support.lang.spider.selector;

import com.alibaba.fastjson2.JSON;
import com.chua.common.support.lang.spider.xsoup.SoupTokenQueue;

import java.util.List;

/**
 * parse json
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class SpiderJson extends PlainText {

    public SpiderJson(List<String> strings) {
        super(strings);
    }

    public SpiderJson(String text) {
        super(text);
    }

    public <T> List<T> toList(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return JSON.parseArray(getFirstSourceText(), clazz);
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        JsonPathSelector jsonPathSelector = new JsonPathSelector(jsonPath);
        return selectList(jsonPathSelector, getSourceTexts());
    }
}
