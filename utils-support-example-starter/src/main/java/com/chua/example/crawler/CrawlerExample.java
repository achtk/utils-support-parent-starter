package com.chua.example.crawler;

import com.chua.common.support.lang.spide.Spider;
import com.chua.common.support.lang.spide.SpiderBuilder;
import com.chua.common.support.lang.spide.listener.Listener;
import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.processor.PageProcessor;
import com.chua.common.support.lang.spide.request.Request;
import com.chua.spider.support.processor.GithubRepoPageProcessor;
import com.chua.spider.support.processor.ZhihuPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * @author CH
 */
public class CrawlerExample {

    public static void main(String[] args) throws Exception {
        Spider spider = SpiderBuilder.newBuilder().addUrl("https://www.zhihu.com/explore")
                .build(page -> {
                    page.addTargetRequests(page.getHtml().links().regex("https://www\\.zhihu\\.com/question/\\d+/answer/\\d+.*").all());
                    page.putField("title", page.getHtml().xpath("//h1[@class='QuestionHeader-title']/text()").toString());
                    page.putField("question", page.getHtml().xpath("//div[@class='QuestionRichText']//tidyText()").toString());
                    page.putField("answer", page.getHtml().xpath("//div[@class='QuestionAnswer-content']/tidyText()").toString());
                    if (page.getResultItems().get("title")==null){
                        //skip this page
                        page.setSkip(true);
                    }
                });

        spider.start();
    }
}
