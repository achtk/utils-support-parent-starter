package com.chua.example.mapping;

import com.chua.common.support.crawler.annotations.XpathQuery;
import com.chua.common.support.mapping.annotation.MappingRequest;

/**
 * @author CH
 */
public class GiteeTitle {

    @XpathQuery("//img/@src")
    private String src;

    @XpathQuery("//h3/a/text()")
    private String title;
}
