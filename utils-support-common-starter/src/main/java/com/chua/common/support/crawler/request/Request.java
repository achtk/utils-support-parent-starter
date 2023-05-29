package com.chua.common.support.crawler.request;

import com.chua.common.support.crawler.event.Event;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * 页面请求
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@Builder
public class Request {
    private String url;
    private String original;
    @Singular("param")
    private Map<String, Object> param;
    @Singular("cookie")
    private Map<String, String> cookie;
    @Singular("header")
    private Map<String, String> header;
    private String userAgent;
    private String referrer;
    private boolean ifPost;
    private boolean isValidateTlsCertificates;
    private PageProxy proxy;
    /**
     *
     */
    @Builder.Default
    private int timeout = 60_000;
    /**
     * 仅htmlunit使用
     */
    @Singular("event")
    private List<Event<?, ?>> event;
}
