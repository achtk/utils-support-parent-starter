package com.chua.common.support.crawler.node;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.nodes.Document;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * simple
 *
 * @author CH
 */
@Data
public class DelegateXpathParser implements XpathParser {

    private Set<String> xpath;
    private BiConsumer<Document, Map<String, List<String>>> consumer;
    private Request pageRequest;
    private CrawlerBuilder crawlerBuilder;

    public DelegateXpathParser(Set<String> xpath, BiConsumer<Document, Map<String, List<String>>> consumer) {
        this.xpath = xpath;
        this.consumer = consumer;
    }

    @Override
    public void parse(Document html, Map<String, List<String>> content) {
        consumer.accept(html, content);
    }

    @Override
    public Set<String> xpath() {
        return xpath;
    }

    @Override
    public Parser newParser(PageLoader pageLoader) {
        return new DelegateXpathParser(xpath, consumer);
    }
}
