package com.chua.common.support.crawler.task;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.UrlCrawUtils;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.process.ParserProcessor;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.url.UrlLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 爬虫任务
 *
 * @author CH
 */
@SuppressWarnings("ALL")
@Slf4j
public final class StandardCrawlerTask implements CrawlerTask {

    private final CrawlerBuilder crawlerBuilder;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final UrlLoader urlLoader;

    public StandardCrawlerTask(CrawlerBuilder crawlerBuilder, UrlLoader urlLoader) {
        this.crawlerBuilder = crawlerBuilder;
        this.urlLoader = urlLoader;
        this.running.set(true);

    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                doAnalysisCrawlerUrl();
            } finally {
                if (crawlerBuilder.pauseMillis() > 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(crawlerBuilder.pauseMillis());
                    } catch (InterruptedException e) {
                        log.info(">>>>>>>>>>> crawler thread is interrupted. {}", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 解析url
     *
     * @return 1: 无效地址, 0: OK, -1: 解析异常
     */
    private int doAnalysisCrawlerUrl() {
        PageLoader pageLoader = crawlerBuilder.pageLoader();
        String name = pageLoader.getClass().getSimpleName();
        String url = name.contains("PcapPageLoader") ? null : urlLoader.getUrl();
        if (log.isDebugEnabled()) {
            log.info(">>>>>>>>>>> crawler, process url : {}", url);
        }

        if (null == url) {
            log.info(">>>>>>>>>>> crawler, wait url");
            return -1;
        }

        // 页面解析器
        List<Parser> pageParser = crawlerBuilder.parsers();
        for (Parser parser : pageParser) {
            parser.setCrawlerBuilder(crawlerBuilder);
        }

        if (pageParser.isEmpty()) {
            log.warn("无解析规则@see builder.addPageParser");
            return -1;
        }
        //解析器信息解析
        List<ParserProcessor> processors = crawlerBuilder.newProcessors();
        // 失败尝试
        for (int i = 0; i < (1 + crawlerBuilder.retry()); i++) {
            boolean ret = false;
            try {
                Request request = UrlCrawUtils.createPageRequest(crawlerBuilder, url);
                for (Parser parser : pageParser) {
                    Parser newParser = parser.newParser(pageLoader);
                    //预处理
                    newParser.setPageRequest(request);
                    newParser.setCrawlerBuilder(crawlerBuilder);
                    for (ParserProcessor processor : processors) {
                        if (processor.matcher(newParser)) {
                            ret = processor.processor(newParser, urlLoader);
                            break;
                        }
                    }

                }

            } catch (Throwable e) {
                log.info(">>>>>>>>>>> crawler proocess error.", e);
            }


            if (ret) {
                urlLoader.addUrl(url);
                break;
            }
        }
        return 0;
    }

    private CrawlerBuilder getConfig() {
        return crawlerBuilder;
    }

    @Override
    public void toStop() {
        running.set(false);
    }

}
