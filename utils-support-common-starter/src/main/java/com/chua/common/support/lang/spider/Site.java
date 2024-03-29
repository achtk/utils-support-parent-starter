package com.chua.common.support.lang.spider;

import com.chua.common.support.lang.spider.utils.BaseHttpConstant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * Object contains setting for crawler.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @seecom.chua.common.support.lang.spider.processor.PageProcessor
 * @since 0.1.0
 */
@Data
@Accessors(chain = true)
public class Site {

    private String domain;

    private String userAgent;

    private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();

    private Map<String, Map<String, String>> cookies = new HashMap<>();

    private String charset;

    private String defaultCharset;

    private int sleepTime = 5000;

    private int retryTimes = 0;

    private int cycleRetryTimes = 0;

    private int retrySleepTime = 1000;

    private int timeOut = 5000;

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

    private Map<String, String> headers = new HashMap<String, String>();

    private boolean useGzip = true;

    private boolean disableCookieManagement = false;


    private String proxyHost;
    private int proxyPort;
    private String proxyScheme;

    static {
        DEFAULT_STATUS_CODE_SET.add(BaseHttpConstant.BaseStatusCode.CODE_200);
    }

    /**
     * new a Site
     *
     * @return new site
     */
    public static Site me() {
        return new Site();
    }

    /**
     * Add a cookie with domain {@link #getDomain()}
     *
     * @param name  name
     * @param value value
     * @return this
     */
    public Site addCookie(String name, String value) {
        defaultCookies.put(name, value);
        return this;
    }

    /**
     * get cookies of all domains
     *
     * @return get cookies
     */
    public Map<String, Map<String, String>> getAllCookies() {
        return cookies;
    }

    /**
     * Add a cookie with specific domain.
     *
     * @param domain domain
     * @param name   name
     * @param value  value
     * @return this
     */
    public Site addCookie(String domain, String name, String value) {
        if (!cookies.containsKey(domain)) {
            cookies.put(domain, new HashMap<>(1 << 4));
        }
        cookies.get(domain).put(name, value);
        return this;
    }


    /**
     * get cookies
     *
     * @return get cookies
     */
    public Map<String, String> getCookies() {
        return defaultCookies;
    }

    /**
     * Put an Http header for downloader. <br>
     * Use {@link #addCookie(String, String)} for cookie and {@link #setUserAgent(String)} for user-agent. <br>
     *
     * @param key   key of http header, there are some keys constant in {@link BaseHttpConstant.BaseHeader}
     * @param value value of header
     * @return this
     */
    public Site addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Task toTask() {
        return new Task() {
            @Override
            public String getUuid() {
                String uuid = Site.this.getDomain();
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString();
                }
                return uuid;
            }

            @Override
            public Site getSite() {
                return Site.this;
            }
        };
    }

}
