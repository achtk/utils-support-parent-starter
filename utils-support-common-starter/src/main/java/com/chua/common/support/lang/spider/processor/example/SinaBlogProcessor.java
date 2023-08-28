package com.chua.common.support.lang.spider.processor.example;

import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Spider;
import com.chua.common.support.lang.spider.processor.PageProcessor;

/**
 * 基础类
 *
 * @author CH
 */
public class SinaBlogProcessor implements PageProcessor {

    public static final String URL_POST = "https://blog.sina.com.cn/s/blog_\\w+\\.html";

    private Site site = Site
            .me()
            .setDomain("blog.sina.com.cn")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    public static void main(String[] args) {
        Spider.create(new SinaBlogProcessor()).addUrl("http://blog.sina.com.cn/s/articlelist_1487828712_0_1.html")
                .run();
    }

    @Override
    public void process(Page page) {
        //列表页
        if (!page.getUrl().regex(URL_POST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//li[@class='feed-only-text']/a").links().regex(URL_POST).all());
            page.putField("title", page.getHtml().xpath("//li[@class='feed-only-text']/p[@class='title']/a/text()"));
            page.putField("content", page.getHtml().xpath("//li[@class='feed-only-text']/p[@class='body']/a/text()"));
            page.putField("date", page.getHtml().xpath("//li[@class='feed-only-text']//div[@class='time']/a/text()"));
        } else {
            //文章页
            page.putField("bodyTitle", page.getHtml().xpath("//div[@class='articalTitle']//h2[@class='titName']/text()").toString());
            page.putField("bodyTime", page.getHtml().xpath("//div[@class='articalTitle']//span[@class='time']/text()"));
            page.putField("bodyContent", page.getHtml().xpath("//div[@class='articalTitle']//div[@class='articalContent']/html()"));
        }
    }
}
