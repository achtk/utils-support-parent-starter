package com.chua.common.support.crawler.entity;


import com.chua.common.support.crawler.annotations.XpathQuery;
import lombok.Data;

import java.util.List;

/**
 * css解析
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@XpathQuery
public class HrefParser {
    /**
     * href
     */
    @XpathQuery("//a/@href")
    private List<String> ahref;

    /**
     * script
     */
    @XpathQuery("//script/@src")
    private List<String> script;

    /**
     * css
     */
    @XpathQuery("//link/@href")
    private List<String> css;
    /**
     * img
     */
    @XpathQuery("//img/@src")
    private List<String> images;
    /**
     * video
     */
    @XpathQuery("//video/@src")
    private List<String> videos;
}
