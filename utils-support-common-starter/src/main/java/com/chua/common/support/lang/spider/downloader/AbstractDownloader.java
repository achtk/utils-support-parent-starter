package com.chua.common.support.lang.spider.downloader;

import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.Request;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Task;
import com.chua.common.support.lang.spider.selector.Html;

/**
 * Base class of downloader with some common methods.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class AbstractDownloader implements Downloader {

    /**
     * A simple method to download a url.
     *
     * @param url url
     * @return html
     */
    public Html download(String url) {
        return download(url, null);
    }

    /**
     * A simple method to download a url.
     *
     * @param url     url
     * @param charset charset
     * @return html
     */
    public Html download(String url, String charset) {
        Page page = download(new Request(url), Site.me().setCharset(charset).toTask());
        return (Html) page.getHtml();
    }

    @Deprecated
    protected void onSuccess(Request request) {
    }

    /**
     * @since 0.7.6
     */
    protected void onSuccess(Request request, Task task) {
        this.onSuccess(request);
    }

    @Deprecated
    protected void onError(Request request) {
    }

    /**
     * @since 0.7.6
     */
    protected void onError(Request request, Task task, Throwable e) {
        this.onError(request);
    }

}
