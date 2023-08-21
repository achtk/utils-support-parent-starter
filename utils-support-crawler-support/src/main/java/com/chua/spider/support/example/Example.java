package com.chua.spider.support.example;


import com.chua.common.support.lang.spide.Spider;
import com.chua.common.support.lang.spide.SpiderBuilder;
import com.chua.spider.support.processor.BaiduBaikePageProcessor;
import com.chua.spider.support.processor.GithubRepoPageProcessor;

/**
 * @author CH
 */
public class Example {

    public static void main(String[] args) {
        Spider spider = SpiderBuilder.newBuilder().addUrl("https://github.com/code4craft")
                .build(new GithubRepoPageProcessor());

        spider.start();
    }
}
