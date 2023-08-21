package com.chua.spider.support.processor;

import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.processor.PageProcessor;
import com.chua.spider.support.page.MagicOrginPage;

/**
 * @author CH
 */
public class ZhihuPageProcessor extends us.codecraft.webmagic.processor.example.ZhihuPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        super.process(new MagicOrginPage(page));
    }
}
