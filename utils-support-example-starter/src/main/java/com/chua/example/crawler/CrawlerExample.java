package com.chua.example.crawler;

import com.chua.common.support.lang.spider.Spider;
import com.chua.common.support.lang.spider.processor.example.SinaBlogProcessor;

/**
 * @author CH
 */
public class CrawlerExample {

    public static void main(String[] args) throws Exception {
        Spider.create(new SinaBlogProcessor())
                .addUrl("https://blog.sina.com.cn/lm/ent/")
                .start();
    }
}
