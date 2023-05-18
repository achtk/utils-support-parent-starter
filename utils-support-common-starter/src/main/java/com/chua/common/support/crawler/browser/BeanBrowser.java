package com.chua.common.support.crawler.browser;

import com.chua.common.support.value.SimpleTypeValue;

import java.util.List;

/**
 * 浏览器
 *
 * @author Administrator
 */
public final class BeanBrowser extends SimpleTypeValue implements Browser{
    public BeanBrowser(List<Object> value) {
        super(value);
    }

    public BeanBrowser(Object... args) {
        super(args);
    }
}
