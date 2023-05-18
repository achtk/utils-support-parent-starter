package com.chua.common.support.crawler.entity;


import com.chua.common.support.crawler.annotations.XpathQuery;

import java.util.List;

/**
 * css解析
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/20
 */
@XpathQuery
public class JsParser {
    /**
     * href
     */
    @XpathQuery("//script/@src")
    private List<String> href;
}
