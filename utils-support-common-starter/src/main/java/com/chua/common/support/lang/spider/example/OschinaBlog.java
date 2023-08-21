package com.chua.common.support.lang.spider.example;

import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.model.OOSpider;
import com.chua.common.support.lang.spider.model.annotation.ExtractBy;
import com.chua.common.support.lang.spider.model.annotation.TargetUrl;
import com.chua.common.support.lang.spider.pipeline.JsonFilePageModelPipeline;

import java.util.Date;
import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.3.2
 */
@TargetUrl("http://my.oschina.net/flashsword/blog/\\d+")
public class OschinaBlog {

    @ExtractBy("//title/text()")
    private String title;

    @ExtractBy(value = "div.BlogContent", type = ExtractBy.Type.Css)
    private String content;

    @ExtractBy(value = "//div[@class='BlogTags']/a/text()", multi = true)
    private List<String> tags;

    @ExtractBy("//div[@class='BlogStat']/regex('\\d+-\\d+-\\d+\\s+\\d+:\\d+')")
    private Date date;

    public static void main(String[] args) {
        //results will be saved to "/data/webmagic/" in json format
        OOSpider.create(Site.me(), new JsonFilePageModelPipeline("/data/webmagic/"), OschinaBlog.class)
                .addUrl("http://my.oschina.net/flashsword/blog").run();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }

    public Date getDate() {
        return date;
    }

}
