package com.chua.common.support.lang.spider.xsoup;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public interface Elements {
    /**
     * 获取节点
     *
     * @return 节点
     */
    String get();

    /**
     * 获取节点
     *
     * @return 节点
     */
    List<String> list();

    /**
     * 获取节点
     *
     * @return 节点
     */
    com.chua.common.support.jsoup.select.Elements getElements();
}
