package com.chua.example.crawler;

import com.chua.common.support.crawler.Crawler;
import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.annotations.XpathQuery;
import com.chua.common.support.crawler.listener.Listener;
import com.chua.common.support.crawler.node.AbstractPageParser;
import com.chua.common.support.crawler.node.DelegateXpathParser;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.request.Response;
import com.chua.common.support.crawler.url.LocalUrlLoader;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.SafeBiConsumer;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author CH
 */
public class CrawlerExample {
    @XpathQuery
    public static class Gitee {

        @XpathQuery("//td/html()")
        private List<String> title;

    }
    @XpathQuery
    public static class Gitee1 {

        @XpathQuery("//div[@class=list_inc]/html()")
        private String name;

    }

    public static void main(String[] args) throws Exception {
        String url = "http://zjfw.zwfwb.zjtz.gov.cn/egov/portals_zj_new/agencylist.jsp?type=second&currentJDBCPage=%s";
        new File("./2.txt").delete();
        FileUtils.write(new File("./2.txt"),
                    "公司名称@" +
                        "统一社会信用代码@" +
                        "资质等级@" +
                        "专业资质名称@" +
                        "主营业务@" +
                        "主管部门@" +
                        "简介@" +
                        "单位性质@" +
                        "营业期限（年）@" +
                        "注册资本（万元）@" +
                        "注册时间@" +
                        "注册登记机关@" +
                        "法定代表人@" +
                        "联系人@" +
                        "联系人手机@" +
                        "地址" +
                        "\r\n", StandardCharsets.UTF_8, true);

        UrlLoader urlLoader = new LocalUrlLoader();
        CrawlerBuilder builder = CrawlerBuilder.builder()
                .urlLoader(urlLoader)
                .addUrl("http://zjfw.zwfwb.zjtz.gov.cn/egov/portals_zj_new/agencylist.jsp?type=second")
                .addParser(new DelegateXpathParser(Collections.singleton("//div[@class=list_inc_tit]/html()"), (SafeBiConsumer<Document, Map<String, List<String>>>) (document, stringListMap) -> {
                    Elements elements = document.selectXpath("//div[@class='list_inc_tit fontht']/a");
                    for (Element element : elements) {
                        Document document1 = JsoupUtil.load(Request.builder().url("http://zjfw.zwfwb.zjtz.gov.cn/egov/portals_zj_new/" + element.attr("href")).build());

                        FileUtils.write(new File("./2.txt"),
                                document1.selectXpath("//div[@class='company_dtr fontht']//h2").text() + "@" +
                                document1.selectXpath("//div[@class='company_dtr fontht']//dd/u[1]").text() + "@" +
                                document1.selectXpath("//div[@class='company_dtr fontht']//dd/u[2]").text() + "@" +
                                document1.selectXpath("//div[@class='company_dtr fontht']//dd/u[3]").text() + "@" +
                                document1.selectXpath("//div[@class='company_dtr fontht']//dd/u[4]").text() + "@" +
                                document1.selectXpath("//div[@class='company_dtr fontht']//dd/u[5]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[1]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[2]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[3]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[4]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[5]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[9]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[14]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[15]/td[1]").text() + "@" +
                                document1.selectXpath("//div[@class='zw_nesmalllisti'][2]//tr[20]/td[1]").text() +
                                        "\r\n", StandardCharsets.UTF_8, true);
                    }
                    System.out.println();
                }))
                .thread(6)
                .allowSpread(false)
                .build();
        for (int i = 1; i < 127; i++) {
            String format = String.format(url, i);
            urlLoader.addUrl(format);

        }
        Crawler crawler = builder.create();
        crawler.start(true);

    }

    public static void main1(String[] args) throws Exception {
        String url = "http://zjfw.zwfwb.zjtz.gov.cn/egov/portals_zj_new/serviceguide.jsp?type=first&currentJDBCPage=%s";
        new File("./1.txt").delete();
        for (int i = 1; i < 10; i++) {
            String format = String.format(url, i);
            CrawlerBuilder builder = CrawlerBuilder.builder()
                    .addUrl(format)
                    .addParser(new AbstractPageParser<Gitee>(){})
                    .thread(1)
                    .allowSpread(false)
                    .listener(new Listener() {
                        @Override
                        public void listen(Response response) {
                            try {
                                Gitee value = (Gitee) response.getValue();
                                List<String> title = value.title;
                                List<String> strings = title.subList(0, 4);
                                //FileUtils.write(new File("./1.txt"), Joiner.on(",").join(strings) + "\r\n", StandardCharsets.UTF_8, true);

                                title = new LinkedList<>(title.subList(0, title.size() - 3));
                                title.removeAll(strings);
                                List<String> item = new LinkedList<>();
                                for (int j = 0; j < title.size(); j++) {
                                    String s = title.get(j).replaceAll("<img(.*?)\\s+(.*?)>|<a(.*?)\\s+(.*?)>|</a>|<span>|</span>", "");
                                    item.add(s);
                                    if(s.contains("：")) {
                                        s = s.substring(s.lastIndexOf("：") + 1);
                                    }

                                    if (NumberUtils.isNumber(s)) {
                                        if(item.size() == 3) {
                                            item.add(1, "");
                                        }
                                        FileUtils.write(new File("./1.txt"), Joiner.on(",").join(item)+ "\r\n", StandardCharsets.UTF_8, true);
                                        item.clear();
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .build();

            Crawler crawler = builder.create();
            crawler.start(true);

        }

    }
}
