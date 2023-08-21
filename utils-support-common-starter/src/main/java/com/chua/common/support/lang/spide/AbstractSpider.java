package com.chua.common.support.lang.spide;

import com.chua.common.support.lang.spide.processor.PageProcessor;
import com.chua.common.support.lang.spide.setting.Setting;

/**
 * 爬虫
 * @author CH
 */
@SuppressWarnings("ALL")
public abstract class AbstractSpider<S> implements Spider<S> {

    protected PageProcessor pageProcessor;
    protected SpiderBuilder builder;
    protected Setting setting;

    public AbstractSpider(SpiderBuilder builder, PageProcessor pageProcessor, Setting setting) {
        this.setting = setting;
        this.builder = builder;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public String getUUID() {
        return builder.uuid();
    }


    protected String[] getUrls() {
        return builder.urls().toArray(new String[0]);
    }
}
