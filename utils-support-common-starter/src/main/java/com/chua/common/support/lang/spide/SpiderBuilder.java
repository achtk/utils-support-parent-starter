package com.chua.common.support.lang.spide;

import com.chua.common.support.lang.spide.downloader.Downloader;
import com.chua.common.support.lang.spide.listener.Listener;
import com.chua.common.support.lang.spide.pipeline.Pipeline;
import com.chua.common.support.lang.spide.processor.PageProcessor;
import com.chua.common.support.lang.spide.scheduler.Scheduler;
import com.chua.common.support.lang.spide.setting.Setting;
import com.chua.common.support.spi.ServiceProvider;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 爬虫
 *
 * @author CH
 */
@Slf4j
@Data
@SuppressWarnings("ALL")
@Accessors(fluent = true)
public class SpiderBuilder {
    /**
     * 下载器
     */
    private Downloader downloader;
    /**
     * 调度器
     */
    private Scheduler scheduler;
    /**
     * 线程数
     */
    private int threadNum = 1;
    /**
     * 管道
     */
    private List<Pipeline> pipelines = new LinkedList<>();
    /**
     * 地址
     */
    private List<String> urls = new LinkedList<>();
    /**
     * 任务ID
     */
    private String uuid;

    /**
     * 线程池
     */
    private ExecutorService executorService;
    /**
     * 完成时是否退出
     */
    private boolean exitWhenComplete = true;
    /**
     * 监视器
     */
    private List<Listener> listeners = new LinkedList<>();
    /**
     * 等待时间
     */
    private long emptySleepTime = 30000;


    public static SpiderBuilder newBuilder() {
        return new SpiderBuilder();
    }

    /**
     * 添加监视器
     *
     * @param listener 监视器
     * @return this
     */
    public SpiderBuilder addListener(Listener listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * 添加管道
     *
     * @param pipeline 管道
     * @return this
     */
    public SpiderBuilder addPipleline(Pipeline pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * 添加管道
     *
     * @param pipeline 管道
     * @return this
     */
    public SpiderBuilder addUrl(String... urls) {
        this.urls.addAll(Arrays.asList(urls));
        return this;
    }

    /**
     * 初始化
     *
     * @param type          类型
     * @param pageProcessor 加载器
     * @param setting       设置
     * @return 爬虫
     */
    public Spider build(String type, PageProcessor pageProcessor, Setting setting) {
        return ServiceProvider.of(Spider.class).getNewExtension(type, this, pageProcessor, setting);
    }
    /**
     * 初始化
     *
     * @param pageProcessor 加载器
     * @param setting       设置
     * @return 爬虫
     */
    public Spider build(PageProcessor pageProcessor, Setting setting) {
        return build("magic", pageProcessor, setting);
    }
    /**
     * 初始化
     *
     * @param pageProcessor 加载器
     * @param setting       设置
     * @return 爬虫
     */
    public Spider build(PageProcessor pageProcessor) {
        return build("magic", pageProcessor, Setting.newSetting());
    }
}
