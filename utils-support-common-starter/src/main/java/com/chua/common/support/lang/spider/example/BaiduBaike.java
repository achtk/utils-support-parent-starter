package com.chua.common.support.lang.spider.example;


import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.model.OoSpider;
import com.chua.common.support.lang.spider.model.annotation.ExtractBy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public class BaiduBaike {

    @ExtractBy("//h1[@class=title]/div[@class=lemmaTitleH1]/text()")
    private String name;

    @ExtractBy("//div[@id='lemmaContent-0']//div[@class='para']/allText()")
    private String description;

    @Override
    public String toString() {
        return "BaiduBaike{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static void main(String[] args) {
        OoSpider ooSpider = OoSpider.create(Site.me().setSleepTime(0), BaiduBaike.class);
        //single download
        String urlTemplate = "http://baike.baidu.com/search/word?word=%s&pic=1&sug=1&enc=utf8";
        BaiduBaike baike = ooSpider.<BaiduBaike>get("http://baike.baidu.com/search/word?word=httpclient&pic=1&sug=1&enc=utf8");
        System.out.println(baike);

        //multidownload
        List<String> list = new ArrayList<String>();
        list.add(String.format(urlTemplate, "风力发电"));
        list.add(String.format(urlTemplate, "太阳能"));
        list.add(String.format(urlTemplate, "地热发电"));
        list.add(String.format(urlTemplate, "地热发电"));
        List<BaiduBaike> resultItemses = ooSpider.<BaiduBaike>getAll(list);
        for (BaiduBaike resultItemse : resultItemses) {
            System.out.println(resultItemse);
        }
        ooSpider.close();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
