package com.chua.common.support.lang.spider.xsoup;

import com.chua.common.support.jsoup.nodes.Element;

/**
 * @author code4crafter@gmail.com
 */
public interface XPathEvaluator {

    XElements evaluate(Element element);

    boolean hasAttribute();
}
