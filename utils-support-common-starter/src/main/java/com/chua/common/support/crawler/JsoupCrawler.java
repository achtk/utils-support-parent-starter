package com.chua.common.support.crawler;

import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.task.CrawlerTask;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * jsoup
 * @author CH
 */
@Slf4j
public class JsoupCrawler implements Crawler{
    /**
     * 检验时间
     */
    private static final int TEST_OF_TIME = 5;
    private CrawlerBuilder crawlerBuilder;

    private final AtomicBoolean status = new AtomicBoolean(false);

    /**
     * 爬虫线程引用镜像
     */
    private final List<CrawlerTask> crawlerCrawlerTasks = new CopyOnWriteArrayList<>();
    /**
     * 最大线程数
     */
    private static final int THREAD_MAX_COUNT = 1000;
    private ExecutorService crawlerService;
    private UrlLoader urlLoader;

    public JsoupCrawler(CrawlerBuilder crawlerBuilder) {
        this.crawlerBuilder = crawlerBuilder;
    }

    @Override
    public void start(boolean async) throws Exception {
        status.set(true);
        this.urlLoader = crawlerBuilder.urlLoader();
        crawlerBuilder.url().forEach(urlLoader::addUrl);
        PageLoader pageLoader = crawlerBuilder.pageLoader();

        int threadCount = crawlerBuilder.thread();

        if (threadCount < 1 || threadCount > THREAD_MAX_COUNT) {
            throw new RuntimeException("crawler threadCount invalid, threadCount : " + threadCount);
        }

        if (pageLoader == null) {
            throw new RuntimeException("crawler pageLoader can not be null.");
        }
        pageLoader.configure(crawlerBuilder);

        if (crawlerBuilder.parsers().isEmpty()) {
            throw new RuntimeException("crawler parsers can not be null.");
        }

        log.info(">>>>>>>>>>> start ...");
        for (int i = 0; i < threadCount; i++) {
            CrawlerTask crawlerThread = newInstance(urlLoader);
            crawlerCrawlerTasks.add(crawlerThread);
        }

        this.crawlerService = crawlerBuilder.crawlerService();
        for (CrawlerTask crawlerTask : crawlerCrawlerTasks) {
            crawlerService.execute(crawlerTask);
        }
        crawlerService.shutdown();

        if (!async) {
            try {
                while (!crawlerService.awaitTermination(TEST_OF_TIME, TimeUnit.SECONDS)) {
                    if (log.isDebugEnabled()) {
                        log.debug(">>>>>>>>>>> crawler still running ...");
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("开始关闭爬虫");
        status.set(false);
        crawlerBuilder.urlLoader().close();
        for (CrawlerTask crawlerCrawlerTask : crawlerCrawlerTasks) {
            crawlerCrawlerTask.toStop();
        }
        ThreadUtils.closeQuietly(crawlerService);
        log.info("爬虫关闭完成");
    }

    @Override
    public void stopIfPrediction(Predicate<Long> predicate) throws Exception {
        Thread thread = ThreadUtils.newThread(new Runnable() {
            @Override
            public void run() {
                while (!predicate.test(urlLoader.visit())) {
                    int index = 0;
                    for (int i = 0; i < 1000; i++) {
                        index = i;
                    }

                    if (index == 1000) {
                        ThreadUtils.sleepSecondsQuietly(0);
                    }
                }

                try {
                    close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, "stop-watcher");
        thread.start();
    }

    @Override
    public void addUrl(String s) {
        urlLoader.addUrl(s);
    }


    private CrawlerTask newInstance(UrlLoader urlLoader) {
        Class<? extends CrawlerTask> crawlerTask1 = crawlerBuilder.taskType();
        try {
            Constructor<? extends CrawlerTask> declaredConstructor = crawlerTask1.getDeclaredConstructor(CrawlerBuilder.class, UrlLoader.class);
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance(crawlerBuilder, urlLoader);
        } catch (Exception ignored) {
        }
        return ClassUtils.forObject(crawlerTask1);
    }
}
