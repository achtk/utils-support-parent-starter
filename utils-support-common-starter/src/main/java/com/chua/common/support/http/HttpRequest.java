package com.chua.common.support.http;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Builder
public class HttpRequest {
    /**
     * 消息体
     */
    @Singular("body")
    private Map<String, Object> body;
    /**
     * 消息体
     */
    private String bodyStr;
    /**
     * 认证
     */
    @Singular("basicAuth")
    private Map<String, String> basicAuth;
    /**
     * 消息头
     */
    private HttpHeader header;
    /**
     * 地址
     */
    private String url;
    /**
     * 超时时间
     */
    @Builder.Default
    private long connectTimeout = 30_000;
    /**
     * 代理
     */
    private String proxy;
    /**
     * 读取超时时间
     */
    private long readTimeout;
    /**
     * 重试次数
     */
    @Builder.Default
    private int retry = 3;
    /**
     * dns
     */
    private String dns;
    /**
     * 最大连接数
     */
    @Builder.Default
    private int maxConnTotal = 50;
    /**
     * 最大路由
     */
    @Builder.Default
    private int maxConnPerRoute = 100;
    /**
     * ssl
     */
    private Object sslSocketFactory;
    /**
     * 客户端
     */
    private Object client;

    /**
     * 是否是表单
     *
     * @return 是否是表单
     */
    public boolean isFormData() {
        for (String value : header.values()) {
            if (value.startsWith(HttpConstant.FORM_DATA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有二进制文件
     *
     * @return 二进制文件
     */
    public boolean hasBin() {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                continue;
            }
            if (value instanceof byte[] || value instanceof File || value instanceof InputStream) {
                return true;
            }
        }
        return false;
    }
}
