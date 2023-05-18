package com.chua.common.support.crawler.url;

import com.chua.common.support.crawler.CrawlerBuilder;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * url加载器
 * @author CH
 */
public final class LocalUrlLoader extends AbstractUrlLoader{

    final Queue<String> urls;

    final Set<String> visited = new CopyOnWriteArraySet<>();

    final AtomicLong count = new AtomicLong(0);

    public LocalUrlLoader() {
        this.urls = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }

    public LocalUrlLoader(CrawlerBuilder crawlerBuilder) {
        super(crawlerBuilder);
        this.urls = new LinkedBlockingQueue<>(crawlerBuilder.urlPool());
    }

    @Override
    public boolean addUrl(String url) {
        if(visited.contains(url)) {
            return false;
        }
        visited.add(url);
        count.incrementAndGet();
        urls.add(url);
        return true;
    }

    @Override
    public UrlLoader removeUrl(String url) {
        urls.remove(url);
        return this;
    }

    @Override
    public String getUrl() {
        return urls.poll();
    }

    @Override
    public long visited() {
        return count.get();
    }

    @Override
    public long visit() {
        return urls.size();
    }

    @Override
    public void reset() {
        urls.clear();
    }

    @Override
    public void close() throws Exception {

    }
}
