package com.chua.spider.support.scheduler;

import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.scheduler.Scheduler;
import com.chua.common.support.lang.spide.task.Task;
import com.chua.spider.support.request.MagicOriginRequest;
import com.chua.spider.support.request.MagicRequest;
import us.codecraft.webmagic.Spider;

/**
 * FileCacheQueueScheduler
 * @see us.codecraft.webmagic.scheduler.FileCacheQueueScheduler
 * @author CH
 */
public class FileCacheQueueScheduler extends us.codecraft.webmagic.scheduler.FileCacheQueueScheduler implements Scheduler<Spider> {

    public FileCacheQueueScheduler(String filePath) {
        super(filePath);
    }

    @Override
    public void push(Request request, Task<Spider> task) {
        super.push(new MagicRequest(request), task.getSpider());
    }

    @Override
    public Request poll(Task<Spider> task) {
        us.codecraft.webmagic.Request request = super.poll(task.getSpider());
        return new MagicOriginRequest(request);
    }
}
