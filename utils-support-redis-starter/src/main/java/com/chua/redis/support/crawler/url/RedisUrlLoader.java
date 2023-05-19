package com.chua.redis.support.crawler.url;

import com.chua.common.support.crawler.url.UrlLoader;
import redis.clients.jedis.Jedis;

/**
 * 申请两个 Redis Key：
 * <pre>
 *      unVisitedUrl：待采集URL池
 *      visitedUrl：已采集URL池
 * </pre>
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/21
 */
public class RedisUrlLoader implements UrlLoader {

    private final Jedis jedis;

    private final String unVisitedUrl = "unVisitedUrl";
    private final String visitedUrl = "visitedUrl";

    public RedisUrlLoader(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean addUrl(String url) {
        boolean aBoolean = jedis.sismember(visitedUrl, url);
        if (!aBoolean) {
            return false;
        }
        jedis.lpush(unVisitedUrl, url);
        return true;
    }

    @Override
    public UrlLoader removeUrl(String url) {
        return this;
    }


    @Override
    public String getUrl() {
        String link = jedis.lpop(unVisitedUrl);
        boolean aBoolean = jedis.sismember(visitedUrl, link);
        if (!aBoolean) {
            return null;
        }
        jedis.sadd(visitedUrl, link);
        return link;
    }

    @Override
    public long visited() {
        return jedis.llen(unVisitedUrl);
    }

    @Override
    public long visit() {
        return jedis.llen(visitedUrl);
    }


    @Override
    public void reset() {
        jedis.del(unVisitedUrl);
        jedis.del(visitedUrl);
    }

    @Override
    public void close() throws Exception {
        if (null != jedis) {
            jedis.close();
        }
    }
}
