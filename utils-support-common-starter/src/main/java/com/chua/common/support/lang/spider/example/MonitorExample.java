package com.chua.common.support.lang.spider.example;

import com.chua.common.support.lang.spider.Spider;
import com.chua.common.support.lang.spider.processor.example.GithubRepoPageProcessor;
import com.chua.common.support.lang.spider.processor.example.ZhihuPageProcessor;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class MonitorExample {

    public static void main(String[] args) throws Exception {

        Spider zhihuSpider = Spider.create(new ZhihuPageProcessor())
                .addUrl("http://my.oschina.net/flashsword/blog");
        Spider githubSpider = Spider.create(new GithubRepoPageProcessor())
                .addUrl("https://github.com/code4craft");

        zhihuSpider.start();
        githubSpider.start();
    }
}
