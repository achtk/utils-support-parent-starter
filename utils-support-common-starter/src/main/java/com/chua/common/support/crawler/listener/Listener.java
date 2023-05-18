package com.chua.common.support.crawler.listener;

import com.chua.common.support.crawler.request.Response;

/**
 * 监听
 * @author CH
 */
public interface Listener {
    /**
     * 监听
     * @param response 响应
     */
    void listen(Response response);
}
