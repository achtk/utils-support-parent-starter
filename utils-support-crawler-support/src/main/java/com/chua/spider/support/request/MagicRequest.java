package com.chua.spider.support.request;

import com.chua.common.support.lang.spide.request.Request;

/**
 * 请求
 * @author CH
 */
public class MagicRequest extends us.codecraft.webmagic.Request  {

    private final Request request;

    public MagicRequest(Request request) {
        this.request = request;
    }

}
