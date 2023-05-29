package com.chua.example.crawler;

import com.chua.common.support.crawler.Crawler;
import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.annotations.XpathQuery;
import com.chua.common.support.crawler.listener.Listener;
import com.chua.common.support.crawler.node.AbstractPageParser;
import com.chua.common.support.crawler.node.JsonApiParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.node.impl.PageHrefParser;
import com.chua.common.support.crawler.page.JsoupPageLoader;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.request.Response;
import com.chua.common.support.crawler.url.LocalUrlLoader;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.SafeBiConsumer;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.htmlunit.support.crawler.parser.SimpleXpathParser;
import com.chua.htmlunit.support.crawler.parser.XpathParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        FileUtils.write(new File("./2.txt"), "公司名称,经营范围,资质等级,地址,联系电话\r\n", StandardCharsets.UTF_8, true);

        UrlLoader urlLoader = new LocalUrlLoader();
        CrawlerBuilder builder = CrawlerBuilder.builder()
                .urlLoader(urlLoader)
                .addUrl("http://zjfw.zwfwb.zjtz.gov.cn/egov/portals_zj_new/agencylist.jsp?type=second")
                .addParser(new SimpleXpathParser(Collections.singleton("//div[@class=list_inc]/html()"), (SafeBiConsumer<Document, Map<String, List<String>>>) (document, stringListMap) -> {
                    Node node = document.childNodes().get(2).childNode(9).childNode(5).childNode(29).childNode(5).childNode(3);
                    List<Node> nodes = node.childNodes();
                    for (Node node1 : nodes) {
                        if(node1 instanceof Element) {
                            String[] html = ((Element) node1).text().split("\\s+");
                            if("首页".equals(html[0])) {
                                continue;
                            }
                            FileUtils.write(new File("./2.txt"),
                                    html[0] + ","
                                            + html[1].replace("经营范围：", "")  + ","
                                            + html[2].replace("资质等级：", "")  + ","
                                            + html[3].replace("地址：", "")  + ","
                                            + html[3].replace("联系电话：", "") +
                                            "\r\n", StandardCharsets.UTF_8, true);

                        }
                    }
                    System.out.println();
                }))
                .thread(1)
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
