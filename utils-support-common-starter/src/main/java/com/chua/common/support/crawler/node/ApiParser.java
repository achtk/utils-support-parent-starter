package com.chua.common.support.crawler.node;

/**
 * api解析
 *
 * @author CH
 * @version 1.0.0
 */
public interface ApiParser extends Parser {

    /**
     * 解析
     *
     * @param url        地址
     * @param pageSource 页面源
     */
    void parse(String url, String pageSource);


}
