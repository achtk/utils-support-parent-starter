package com.chua.common.support.protocol.server.request;

/**
 * 响应
 *
 * @author CH
 */
public interface Response {

    /**
     * 消息体
     *
     * @return 消息体
     */
    Object getBody();

    /**
     * 消息头
     *
     * @return 消息头
     */
    String getContentType();
}
