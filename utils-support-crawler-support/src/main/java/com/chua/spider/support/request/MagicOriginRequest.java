package com.chua.spider.support.request;

import com.chua.common.support.lang.spide.request.Request;

/**
 * 请求
 * @author CH
 */
public class MagicOriginRequest implements Request  {

    private final us.codecraft.webmagic.Request request;

    public MagicOriginRequest(us.codecraft.webmagic.Request request) {
        this.request = request;
    }
}
