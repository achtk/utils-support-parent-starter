package com.chua.proxy.support.factory;

import com.chua.common.support.request.WebServerRequest;

/**
 * 渠道工厂
 *
 * @author CH
 * @since 2023/09/16
 */
public interface ChannelFactory {


    /**
     * handler
     *
     * @param request 要求
     */
    void handle(WebServerRequest request);
}
