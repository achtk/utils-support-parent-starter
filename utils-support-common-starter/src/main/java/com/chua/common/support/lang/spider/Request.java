package com.chua.common.support.lang.spider;

import com.chua.common.support.lang.spider.downloader.Downloader;
import com.chua.common.support.lang.spider.request.HttpRequestBody;
import com.chua.common.support.lang.spider.utils.Experimental;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Data
@Accessors(chain = true)
public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    private String url;

    private String method;

    private HttpRequestBody requestBody;

    /**
     * this req use this downloader
     */
    private Downloader downloader;

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    /**
     * cookies for current url, if not set use Site's cookies
     */
    private Map<String, String> cookies = new HashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Priority of the request.<br>
     * The bigger will be processed earlier. <br>
     *
     * @seecom.chua.common.support.lang.spider.scheduler.PriorityScheduler
     */
    private long priority;

    /**
     * When it is set to TRUE, the downloader will not try to parse response body to text.
     */
    private boolean binaryContent = false;

    private String charset;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    public long getPriority() {
        return priority;
    }

    /**
     * Set the priority of request for sorting.<br>
     * Need a scheduler supporting priority.<br>
     *
     * @param priority priority
     * @return this
     * @seecom.chua.common.support.lang.spider.scheduler.PriorityScheduler
     */
    @Experimental
    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return (T) extras.get(key);
    }

    public <T> Request putExtra(String key, T value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    public Request addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public Request addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

}
