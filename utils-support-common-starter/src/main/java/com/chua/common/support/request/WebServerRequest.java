package com.chua.common.support.request;

import com.chua.common.support.http.HttpHeader;
import com.chua.common.support.http.HttpHeaders;

/**
 * web服务器请求
 *
 * @author CH
 * @since 2023/09/16
 */
public interface WebServerRequest {


    /**
     * 获取频道
     *
     * @return {@link T}
     */
    <T> T getChannel();

    /**
     * uri
     *
     * @return {@link String}
     */
    String uri();

    /**
     * 消息头
     *
     * @return {@link HttpHeaders}
     */
    HttpHeader headers();


    /**
     * 获取属性
     *
     * @return {@link Attribute}
     */
    Attribute getAttribute();

    /**
     * 获取属性
     *
     * @param name 名称
     * @return {@link T}
     */
    <T> T getAttribute(String name);

    /**
     * 获取要求
     *
     * @return {@link T}
     */
    <T> T getRequest();
}
