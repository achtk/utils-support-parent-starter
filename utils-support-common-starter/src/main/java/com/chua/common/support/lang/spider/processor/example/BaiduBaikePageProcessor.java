package com.chua.common.support.lang.spider.processor.example;

import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.ResultItems;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Spider;
import com.chua.common.support.lang.spider.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
public class BaiduBaikePageProcessor implements PageProcessor {

    private Site site = Site.me()
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
    public void process(Page page) {
        page.putField("name", page.getHtml().css("dl.lemmaWgt-lemmaTitle h1", "text").toString());
        page.putField("description", page.getHtml().xpath("//div[@class='lemma-summary']/allText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //single download
        Spider spider = Spider.create(new BaiduBaikePageProcessor()).thread(2);
        String urlTemplate = "http://baike.baidu.com/search/word?word=%s&pic=1&sug=1&enc=utf8";
        ResultItems resultItems = spider.<ResultItems>get(String.format(urlTemplate, "水力发电"));
        System.out.println(resultItems);

        //multidownload
        List<String> list = new ArrayList<String>();
        list.add(String.format(urlTemplate, "风力发电"));
        list.add(String.format(urlTemplate, "太阳能"));
        list.add(String.format(urlTemplate, "地热发电"));
        list.add(String.format(urlTemplate, "地热发电"));
        List<ResultItems> resultItemses = spider.<ResultItems>getAll(list);
        for (ResultItems resultItemse : resultItemses) {
            System.out.println(resultItemse.getAll());
        }
        spider.close();
    }
}
