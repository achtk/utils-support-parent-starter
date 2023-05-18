package com.chua.common.support.crawler.process;


import com.chua.common.support.crawler.Crawler;
import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.listener.Listener;
import com.chua.common.support.crawler.node.ApiParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.request.Response;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.jsoup.nodes.Document;

/**
 * api加载器
 *
 * @author CH
 * @version 1.0.0
 */
public class ApiParserProcessor implements ParserProcessor {

    private ApiParser apiParser;
    private Crawler crawler;
    private Request pageRequest;
    private Document document;
    private Class<?> classType;
    private CrawlerBuilder config;


    @Override
    public boolean matcher(Parser parser) {
        if (parser instanceof ApiParser) {
            this.apiParser = (ApiParser) parser;
            this.pageRequest = parser.getPageRequest();
            this.config = parser.getCrawlerBuilder();
            return true;
        }
        return false;
    }

    @Override
    public boolean processor(Parser parser, UrlLoader urlLoader) {
        String pareses = null;
        try {
            pareses = JsoupUtil.loadPageSource(pageRequest);
        } catch (Throwable e) {
            urlLoader.addUrl(pageRequest.getOriginal());
        }

        if (pareses == null) {
            return false;
        }
        Listener listener = parser.getCrawlerBuilder().listener();
        if(null != listener) {
            listener.listen(new Response("", parser, pareses));
        }
        apiParser.parse(pageRequest.getUrl(), pareses);
        return true;
    }

}
