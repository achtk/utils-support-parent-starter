package com.chua.common.support.crawler;

import com.chua.common.support.crawler.event.Event;
import com.chua.common.support.crawler.event.PreEvent;
import com.chua.common.support.crawler.listener.Listener;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.page.JsoupPageLoader;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.process.ApiParserProcessor;
import com.chua.common.support.crawler.process.ParserProcessor;
import com.chua.common.support.crawler.request.PageProxy;
import com.chua.common.support.crawler.task.CrawlerTask;
import com.chua.common.support.crawler.task.StandardCrawlerTask;
import com.chua.common.support.crawler.url.LocalUrlLoader;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.common.support.utils.UrlUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 基础配置
 */
@SuppressWarnings("ALL")
@Data
@Accessors(fluent = true)
@Builder
public class CrawlerBuilder {

    /**
     * 页面加载器
     */
    @Builder.Default
    private PageLoader pageLoader = new JsoupPageLoader();

    /**
     * 监听
     */
    private Listener listener;
    /**
     * 链接超时时间
     */
    @Builder.Default
    private int connectionTimeoutMillis = 10_000;

    /**
     * 线程数
     */
    @Builder.Default
    private int thread = 1;
    /**
     * 允许深度爬取
     */
    private boolean allowSpread;

    /**
     * url最大数量
     */
    @Builder.Default
    private int urlPool = Integer.MAX_VALUE;

    /**
     * 停顿时间，爬虫线程处理完页面之后进行主动停顿，避免过于频繁被拦截；
     */
    @Builder.Default
    private int pauseMillis = 5000;

    /**
     * 爬虫线程池
     */
    @Builder.Default
    private final ExecutorService crawlerService = ThreadUtils.newCachedThreadPool();
    @Builder.Default
    private UrlLoader urlLoader = new LocalUrlLoader();
    @Builder.Default
    private Class<? extends CrawlerTask> taskType = StandardCrawlerTask.class;
    @Singular("addWhiteUrl")
    private List<String> whiteUrlRegex;

    @Singular("addParser")
    private List<Parser> parsers;

    public CrawlerBuilder addParser(Class<? extends Parser> parser) {
        Parser forObject = ClassUtils.forObject(parser);
        if (null == forObject) {
            throw new NullPointerException();
        }
        this.parsers.add(forObject);
        return this;
    }

    /**
     * 尝试次数
     */
    @Builder.Default
    private int retry = 1;


    @Singular("addParam")
    private Map<String, Object> param;


    @Singular("addCookie")
    private Map<String, String> cookie;

    @Singular("addHeader")
    private Map<String, String> header;

    /**
     * url
     */
    @Singular("addUrl")
    private List<String> url;

    /**
     * user-agent
     */
    @Singular("addUserAgent")
    public List<String> userAgent;

    private String referrer;
    private boolean ifPost;
    private boolean isValidateTlsCertificates;
    private PageProxy proxy;

    /**
     * 使用
     */
    @Singular("addEvent")
    private List<Event<?, ?>> event;
    /**
     * 使用
     */
    @Singular("addPreEvent")
    private List<PreEvent> preEvent;
    /**
     * 进程处理器
     */
    @Singular("addProcessor")
    public List<ParserProcessor> processors;

    public CrawlerBuilder addProcessor(ParserProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    /**
     * valid url, include white url
     *
     * @param link 链接
     * @return boolean
     */
    public boolean validWhiteUrl(String link) {
        if (!UrlUtils.isUrl(link)) {
            // false if url invalid
            return false;
        }

        if (whiteUrlRegex != null && whiteUrlRegex.size() > 0) {
            boolean underWhiteUrl = false;
            for (String whiteRegex : this.whiteUrlRegex) {
                if (whiteRegex.contains("*")) {
                    if (PathMatcher.APACHE_INSTANCE.match(whiteRegex, link)) {
                        underWhiteUrl = true;
                    }
                    continue;
                }

                if (RegexUtil.matches(whiteRegex, link)) {
                    underWhiteUrl = true;
                }
            }
            return underWhiteUrl;
        }
        return true;
    }

    public List<ParserProcessor> newProcessors() {
        List<ParserProcessor> result = new LinkedList<>();
        processors.forEach(it -> {
            ParserProcessor parser = ClassUtils.forObjectReturn(it.getClass(), ParserProcessor.class);
            if (null != parser) {
                result.add(parser);
            }
        });
        return result;
    }

    /**
     * 初始化
     *
     * @return 初始化
     */
    public Crawler create() {
        if(this.processors.isEmpty()) {
            List<ParserProcessor> tpl = new LinkedList<>();
            Object forObject = ClassUtils.forObject("com.chua.htmlunit.support.crawler.process.PageParserProcessor");
            if (null != forObject) {
                tpl.add((ParserProcessor) forObject);
            }
            Object forObject1 = ClassUtils.forObject("com.chua.htmlunit.support.crawler.process.WindowParserProcessor");
            if (null != forObject1) {
                tpl.add((ParserProcessor) forObject1);
            }
            tpl.add(new ApiParserProcessor());
            this.processors(tpl);
        }
        return new JsoupCrawler(this);
    }
}
