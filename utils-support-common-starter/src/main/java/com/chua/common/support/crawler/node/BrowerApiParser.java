package com.chua.common.support.crawler.node;

import com.chua.common.support.crawler.browser.Browser;

/**
 * api解析
 *
 * @author CH
 * @version 1.0.0
 */
public interface BrowerApiParser extends Parser {

    /**
     * 解析
     *
     * @param url        地址
     * @param pageSource 页面源
     */
    void parse(String url, String pageSource);

    /**
     * 浏览器
     * @return 浏览器
     */
    Browser getBrowner();
}
