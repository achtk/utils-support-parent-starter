package com.chua.common.support.http;

import java.util.Map;

import static com.chua.common.support.http.HttpConstant.CONTENT_TYPE_FORM;

/**
 * http构造器
 *
 * @author CH
 */
public interface HttpClientBuilder {
    /**
     * header
     *
     * @return this
     */
    default HttpClientBuilder isJson() {
        return header("Content-Type", "application/json");
    }


    /**
     * 是form
     *
     * @return {@link HttpClientBuilder}
     */
    default HttpClientBuilder isForm() {
        return header("Content-Type", CONTENT_TYPE_FORM);
    }

    /**
     * proxy
     *
     * @param proxy proxy
     * @return this
     */
    HttpClientBuilder proxy(String proxy);

    /**
     * dns
     *
     * @param dns dns
     * @return this
     */
    HttpClientBuilder dns(String dns);

    /**
     * maxConnTotal
     *
     * @param maxConnTotal maxConnTotal
     * @return this
     */
    HttpClientBuilder maxConnTotal(int maxConnTotal);

    /**
     * maxConnPerRoute
     *
     * @param maxConnPerRoute maxConnPerRoute
     * @return this
     */
    HttpClientBuilder maxConnPerRoute(int maxConnPerRoute);

    /**
     * client
     *
     * @param client client
     * @return this
     */
    HttpClientBuilder client(Object client);

    /**
     * ssl
     *
     * @param sslSocketFactory ssl
     * @return this
     */
    HttpClientBuilder ssl(Object sslSocketFactory);

    /**
     * header
     *
     * @param headerName  消息头名称
     * @param headerValue 消息头值
     * @return this
     */
    HttpClientBuilder header(String headerName, String headerValue);
    /**
     * header
     *
     * @param headers  消息头
     * @return this
     */
    default HttpClientBuilder header(HttpHeader headers) {
        if(null != headers) {
            headers.forEach((s, s2) -> header(s, s2));
        }
        return this;
    }
    /**
     * header
     *
     * @param headers  消息头
     * @return this
     */
    default HttpClientBuilder header(Map<String, String> headers) {
        if(null != headers) {
            headers.forEach(this::header);
        }
        return this;
    }

    /**
     * header
     *
     * @param basicAuthName  认证名称
     * @param basicAuthValue 认证头值
     * @return this
     */
    HttpClientBuilder basicAuth(String basicAuthName, String basicAuthValue);

    /**
     * body
     *
     * @param body 消息
     * @return this
     */
    HttpClientBuilder body(String body);

    /**
     * body
     *
     * @param body 消息
     * @return this
     */
    HttpClientBuilder body(byte[] body);

    /**
     * body
     *
     * @param bodyName  消息头名称
     * @param bodyValue 消息头值
     * @return this
     */
    HttpClientBuilder body(String bodyName, Object bodyValue);

    /**
     * body
     *
     * @param bodys 消息头
     * @return this
     */
    default HttpClientBuilder body(Map<String, ?> bodys) {
        if (null != bodys) {
            bodys.forEach(this::body);
        }
        return this;
    }

    /**
     * url
     *
     * @param url url
     * @return this
     */
    HttpClientBuilder url(String url);

    /**
     * 连接超时时间
     *
     * @param timeout 连接超时时间
     * @return this
     */
    HttpClientBuilder connectTimout(long timeout);

    /**
     * 读取超时时间
     *
     * @param timeout 读取超时时间
     * @return this
     */
    HttpClientBuilder readTimout(long timeout);

    /**
     * 重试次数
     *
     * @param retry 重试次数
     * @return this
     */
    HttpClientBuilder retry(int retry);

    /**
     * 执行器
     *
     * @param type 类型
     * @return 执行器
     */
    HttpClientInvoker newInvoker(String type);

    /**
     * 执行器
     *
     * @return 执行器
     */
    default HttpClientInvoker newInvoker() {
        return newInvoker("httpclient");
    }

}
