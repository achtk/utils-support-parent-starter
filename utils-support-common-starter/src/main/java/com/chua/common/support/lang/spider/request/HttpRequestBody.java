package com.chua.common.support.lang.spider.request;

/**
 * 请求体
 *
 * @author CH
 */
public interface HttpRequestBody {
    /**
     * 消息体
     *
     * @return 消息体
     */
    byte[] getBody();

    /**
     * ContentType
     *
     * @return ContentType
     */
    String getContentType();
}
