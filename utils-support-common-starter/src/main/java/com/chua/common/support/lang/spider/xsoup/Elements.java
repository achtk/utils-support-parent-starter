package com.chua.common.support.lang.spider.xsoup;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public interface Elements {

    String get();

    List<String> list();

    com.chua.common.support.jsoup.select.Elements getElements();
}
