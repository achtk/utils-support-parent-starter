package com.chua.spider.support.processor;

/**
 * @author CH
 */
public class ZhihuPageProcessor extendscom.chua.common.support.lang.spider.processor.example.ZhihuPageProcessor implements PageProcessor{
@Override
public void process(Page page){
        super.process(new MagicOrginPage(page));
        }
        }
