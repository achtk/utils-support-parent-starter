package com.chua.common.support.crawler.node;


import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;

/**
 * 页面解析器
 *
 * @author CH
 * @version 1.0.0
 */
public interface PageParser<T> extends Parser {
    /**
     * 解析页面实体
     *
     * @param html          page html data
     * @param pageVoElement pageVo html data
     * @param pageVo        pageVo object
     * @return 实体
     */
    default T parse(Document html, Element pageVoElement, T pageVo) {
        return pageVo;
    }
}
