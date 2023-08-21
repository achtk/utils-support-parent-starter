package com.chua.common.support.lang.spider;

import com.chua.common.support.lang.spider.selector.Html;
import com.chua.common.support.lang.spider.selector.Json;
import com.chua.common.support.lang.spider.selector.Selectable;
import com.chua.common.support.lang.spider.utils.HttpConstant;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Object storing extracted result and urls to fetch.<br>
 * Not thread safe.<br>
 * Main method：                                               <br>
 * {@link #getUrl()} get url of current page                   <br>
 * {@link #getHtml()}  get content of current page                 <br>
 * {@link #putField(String, Object)}  save extracted result            <br>
 * {@link #getResultItems()} get extract results to be used in {@linkcom.chua.common.support.lang.spider.pipeline.Pipeline}<br>
 * {@link #addTargetRequests(Iterable)} {@link #addTargetRequest(String)} add urls to fetch                 <br>
 *
 * @author code4crafter@gmail.com <br>
 * @seecom.chua.common.support.lang.spider.downloader.Downloader
 * @seecom.chua.common.support.lang.spider.processor.PageProcessor
 * @since 0.1.0
 */
public class Page {

    private Request request;

    private ResultItems resultItems = new ResultItems();

    private Html html;

    private Json json;

    private String rawText;

    private Selectable url;

    private Map<String, List<String>> headers;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private boolean downloadSuccess = true;

    private byte[] bytes;

    private List<Request> targetRequests = new ArrayList<Request>();

    private String charset;

    public Page() {
    }

    public static Page fail() {
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    public Page setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    /**
     * store extract results
     *
     * @param key   key
     * @param field field
     */
    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    /**
     * get html content of page
     *
     * @return html
     */
    public Html getHtml() {
        if (html == null) {
            html = new Html(rawText, request.getUrl());
        }
        return html;
    }

    /**
     * get json content of page
     *
     * @return json
     * @since 0.5.0
     */
    public Json getJson() {
        if (json == null) {
            json = new Json(rawText);
        }
        return json;
    }

    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     */
    public void addTargetRequests(Iterable<String> requests) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s));
        }
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     * @param priority priority
     */
    public void addTargetRequests(Iterable<String> requests, long priority) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s).setPriority(priority));
        }
    }

    /**
     * add url to fetch
     *
     * @param requestString requestString
     */
    public void addTargetRequest(String requestString) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        requestString = UrlUtils.canonicalizeUrl(requestString, url.toString());
        targetRequests.add(new Request(requestString));
    }

    /**
     * add requests to fetch
     *
     * @param request request
     */
    public void addTargetRequest(Request request) {
        targetRequests.add(request);
    }

    /**
     * get url of current page
     *
     * @return url of current page
     */
    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRawText() {
        return rawText;
    }

    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + resultItems +
                ", html=" + html +
                ", json=" + json +
                ", rawText='" + rawText + '\'' +
                ", url=" + url +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                ", downloadSuccess=" + downloadSuccess +
                ", targetRequests=" + targetRequests +
                ", charset='" + charset + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
