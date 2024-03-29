package com.chua.common.support.lang.spider.example;


import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.model.ConsolePageModelPipeline;
import com.chua.common.support.lang.spider.model.HasKey;
import com.chua.common.support.lang.spider.model.OoSpider;
import com.chua.common.support.lang.spider.model.annotation.ExtractBy;
import com.chua.common.support.lang.spider.model.annotation.ExtractByUrl;

import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.1
 */
public class GithubRepoApi implements HasKey {

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$.name", source = ExtractBy.Source.RawText)
    private String name;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..owner.login", source = ExtractBy.Source.RawText)
    private String author;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$.language", multi = true, source = ExtractBy.Source.RawText)
    private List<String> language;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$.stargazers_count", source = ExtractBy.Source.RawText)
    private int star;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$.forks_count", source = ExtractBy.Source.RawText)
    private int fork;

    @ExtractByUrl
    private String url;

    public static void main(String[] args) {
        OoSpider.create(Site.me().setSleepTime(100)
                        , new ConsolePageModelPipeline(), GithubRepoApi.class)
                .addUrl("https://api.github.com/repos/code4craft/webmagic").run();
    }

    @Override
    public String key() {
        return author + ":" + name;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public int getStar() {
        return star;
    }

    public int getFork() {
        return fork;
    }
}
