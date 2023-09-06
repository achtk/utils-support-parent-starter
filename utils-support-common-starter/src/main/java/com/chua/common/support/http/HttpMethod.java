package com.chua.common.support.http;

/**
 * 请求类型
 *
 * @author CH
 */
public enum HttpMethod {
    /**
     * get
     */
    GET,

    /**
     * get
     */
    GET_RESPONSE,
    /**
     * post
     */
    POST,
    /**
     * post
     */
    POST_FORM_RESPONSE,
    /**
     * post
     */
    POST_STRING_RESPONSE,
    /**
     * post
     */
    POST_FORM,
    /**
     * post
     */
    POST_STRING,
    /**
     * post
     */
    POST_BYTES,
    /**
     * delete
     */
    DELETE,
    /**
     * put
     */
    PUT,
    /**
     * PUT
     */
    PUT_FORM,
    /**
     * PUT
     */
    PUT_STRING,
    /**
     * PUT
     */
    PUT_BYTES,
    /**
     * header
     */
    HEAD,
    /**
     * patch
     */
    PATCH,
    /**
     * option
     */
    OPTION
}
