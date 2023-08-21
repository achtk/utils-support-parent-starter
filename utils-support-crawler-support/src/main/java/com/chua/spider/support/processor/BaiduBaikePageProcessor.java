package com.chua.spider.support.processor;

/**
 * @author CH
 */
public class BaiduBaikePageProcessor extendscom.chua.common.support.lang.spider.processor.example.BaiduBaikePageProcessor implements PageProcessor{
@Override
public void process(Page page){
        super.process(new MagicOrginPage(page));
        }
        }
