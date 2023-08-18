package com.chua.example.other;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 构造器
 *
 * @author CH
 */
@Data
public class HttpInvokeBuilder {
    private final HttpMethod httpMethod;
    public boolean https;
    private static final String FORM_DATA = "multipart/form-data";

    private String url;
    /**
     * 消息体
     */
    private String bodyStr;
    /**
     * 消息体
     */
    @Singular("body")
    private Map<String, Object> body = new LinkedHashMap<>();
    /**
     * 消息头
     */
    @Singular("body")
    private Map<String, String> header = new LinkedHashMap<>();
    /**
     * ssl
     */
    private Object sslSocketFactory;
    /**
     * 超时时间
     */
    @Builder.Default
    private long connectTimeout = 30_000;
    /**
     * 读取超时时间
     */
    private long readTimeout;
    /**
     * 认证
     */
    private Map<String, String> basicAuth = new LinkedHashMap<>();

    /**
     * Https
     *
     * @return this
     */
    public HttpInvokeBuilder isHttps() {
        this.https = true;
        return this;
    }

    /**
     * 添加消息体
     *
     * @param url 名称
     * @return this
     */
    public HttpInvokeBuilder url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加消息体
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    public HttpInvokeBuilder body(String name, Object value) {
        body.put(name, value);
        return this;
    }

    /**
     * 添加消息体
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    public HttpInvokeBuilder header(String name, String value) {
        header.put(name, value);
        return this;
    }
    /**
     * 添加消息体
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    public HttpInvokeBuilder basicAuth(String name, String value) {
        basicAuth.put(name, value);
        return this;
    }

    /**
     * 执行
     *
     * @return 执行
     */
    public Invoker newInvoker() {
        return new DefaultUrlInvoker(this);
    }

    public HttpInvokeBuilder(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }


    /**
     * 是否是表单
     *
     * @return 是否是表单
     */
    public boolean isFormData() {
        for (String value : header.values()) {
            if (value.startsWith(FORM_DATA)) {
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
