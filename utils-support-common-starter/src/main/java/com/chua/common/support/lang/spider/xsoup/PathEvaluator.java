package com.chua.common.support.lang.spider.xsoup;

import com.chua.common.support.jsoup.nodes.Element;

/**
 * @author code4crafter@gmail.com
 */
public interface PathEvaluator {
    /**
     * 解析节点
     *
     * @param element 节点
     * @return 结果
     */
    Elements evaluate(Element element);

    /**
     * 是否包含属性
     *
     * @return 是否包含属性
     */
    boolean hasAttribute();
}
