package com.chua.spider.support.scheduler;

import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.scheduler.Scheduler;
import com.chua.common.support.lang.spide.task.Task;
import com.chua.spider.support.request.MagicOriginRequest;
import com.chua.spider.support.request.MagicRequest;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;

/**
 * @author CH
 */
public class RedisPriorityScheduler extends us.codecraft.webmagic.scheduler.RedisPriorityScheduler implements Scheduler<Spider> {
    public RedisPriorityScheduler(String host) {
        super(host);
    }

    public RedisPriorityScheduler(JedisPool pool) {
        super(pool);
    }


    @Override
    public void push(Request request, Task<Spider> task) {
        super.push(new MagicRequest(request), task.getSpider());
    }

    @Override
    public Request poll(Task<us.codecraft.webmagic.Spider> task) {
        us.codecraft.webmagic.Request request = super.poll(task.getSpider());
        return new MagicOriginRequest(request);
    }
}
