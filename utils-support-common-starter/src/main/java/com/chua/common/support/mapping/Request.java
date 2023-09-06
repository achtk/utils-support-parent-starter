package com.chua.common.support.mapping;

import com.chua.common.support.http.HttpMethod;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Builder
public class Request {

    /**
     * 地址
     */
    private String address;
    /**
     * 负载均衡
     */
    @Builder.Default
    private String balance = "round";

    /**
     * url
     */
    private String url;

    /**
     * 超时
     */
    @Builder.Default
    private int readTimeout = 30_000;

    /**
     * 超时
     */
    @Builder.Default
    private int connectTimeout = 10_000;
    /**
     * body
     */
    @Singular("body")
    private Map<String, Object> body;
    /**
     * header
     */
    @Singular("header")
    private Map<String, String> header;
    /**
     * 方法
     */
    @Builder.Default
    private String method = HttpMethod.GET.name();


    /**
     * 援引
     */
    @Builder.Default
    private String invokeType = "default";
}
