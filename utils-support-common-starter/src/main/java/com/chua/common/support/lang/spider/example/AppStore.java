package com.chua.common.support.lang.spider.example;


import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.model.OOSpider;
import com.chua.common.support.lang.spider.model.annotation.ExtractBy;
import com.chua.common.support.lang.spider.utils.Experimental;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.4.1
 */
@Experimental
public class AppStore {

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..trackName")
    private String trackName;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..description")
    private String description;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..userRatingCount")
    private int userRatingCount;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..screenshotUrls")
    private List<String> screenshotUrls;

    @ExtractBy(type = ExtractBy.Type.JSON_PATH, value = "$..supportedDevices")
    private List<String> supportedDevices;

    public static void main(String[] args) {
        AppStore appStore = OOSpider.create(Site.me(), AppStore.class).<AppStore>get("http://itunes.apple.com/lookup?id=653350791&country=cn&entity=software");
        System.out.println(appStore.trackName);
        System.out.println(appStore.description);
        System.out.println(appStore.userRatingCount);
        System.out.println(appStore.screenshotUrls);
        System.out.println(appStore.supportedDevices);
    }
}
