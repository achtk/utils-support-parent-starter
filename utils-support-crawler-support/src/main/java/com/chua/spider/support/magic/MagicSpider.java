package com.chua.spider.support.magic;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.http.HttpStatus;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.spide.AbstractSpider;
import com.chua.common.support.lang.spide.SpiderBuilder;
import com.chua.common.support.lang.spide.listener.Listener;
import com.chua.common.support.lang.spide.pipeline.Pipeline;
import com.chua.common.support.lang.spide.processor.PageProcessor;
import com.chua.common.support.lang.spide.setting.Setting;
import com.chua.common.support.utils.ClassUtils;
import com.chua.spider.support.page.MagicPage;
import com.chua.spider.support.request.MagicOriginRequest;
import com.chua.spider.support.request.MagicRequest;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * webMagic
 * @author CH
 */
public class MagicSpider extends AbstractSpider<Spider> {

    private Spider spider;


    public MagicSpider(SpiderBuilder builder, PageProcessor pageProcessor, Setting setting) {
        super(builder, pageProcessor, setting);
    }

    @Override
    public void run() {

    }

    @Override
    public void start() {
        this.spider = Spider.create(new us.codecraft.webmagic.processor.PageProcessor() {
            @Override
            public void process(Page page) {
                pageProcessor.process(new MagicPage(page));
            }

            @Override
            public Site getSite() {
                Site site = Site.me();
                BeanUtils.copyProperties(setting, site);
                site.setAcceptStatCode(setting.getAcceptStatCode().stream().map(HttpStatus::value).collect(Collectors.toSet()));
                return site;
            }
        });
        spider.addUrl(super.getUrls());
        spider.setExitWhenComplete(super.builder.exitWhenComplete());
        spider.setUUID(getUUID());
        spider.thread(super.builder.threadNum());
        spider.setEmptySleepTime(super.builder.emptySleepTime());
        spider.setExecutorService(super.builder.executorService());
        if(null != builder.downloader()) {
            spider.setDownloader((Downloader) builder.downloader());
        }

        if(null != builder.scheduler()) {
            spider.setScheduler((Scheduler) builder.scheduler());
        }

        com.chua.common.support.lang.spide.task.Task<Spider> _this = this;
        for (Pipeline<Spider> pipeline : builder.pipelines()) {
            spider.addPipeline((resultItems, task) -> {
                pipeline.process(BeanUtils.copyProperties(resultItems, com.chua.common.support.lang.spide.pipeline.ResultItems.class)
                        , _this);
            });
        }
        spider.setSpiderListeners(builder.listeners().stream().map(it -> {
            return new SpiderListener() {
                @Override
                public void onSuccess(Request request) {
                    it.onSuccess(new MagicOriginRequest(request));
                }

                @Override
                public void onError(Request request) {
                    it.onError(new MagicOriginRequest(request));
                }

                @Override
                public void onError(Request request, Exception e) {
                    it.onError(new MagicOriginRequest(request), e);
                }
            };
        }).collect(Collectors.toList()));

        spider.start();
    }

    @Override
    public void stop() {
        spider.stop();
    }

    @Override
    public Spider getSpider() {
        return spider;
    }
}

