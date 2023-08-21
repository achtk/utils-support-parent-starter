package com.chua.common.support.lang.spider.downloader;

import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.Request;
import com.chua.common.support.lang.spider.Task;
import com.chua.common.support.lang.spider.proxy.ProxyProvider;
import com.chua.common.support.lang.spider.selector.PlainText;
import com.chua.common.support.lang.spider.utils.CharsetUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * url下载器
 *
 * @author CH
 */
@Slf4j
public class JsoupDownloader extends AbstractDownloader {


    private ProxyProvider proxyProvider;

    private boolean responseHeader = true;

    @Override
    public Page download(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        try {
            Document document = Jsoup.parse(new URL(request.getUrl()), task.getSite().getTimeOut());
            Page page = new Page();
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.setStatusCode(200);
            page.setDownloadSuccess(true);
            onSuccess(request, task);
            log.info("downloading page success {}", request.getUrl());

            return page.setRawText(document.text());
        } catch (IOException e) {

            onError(request, task, e);
            log.info("download page {} error", request.getUrl(), e);

            return Page.fail();
        }
    }


    @Override
    public void setThread(int thread) {
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes, Task task) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Optional.ofNullable(task.getSite().getDefaultCharset()).orElseGet(Charset.defaultCharset()::name);
            log.info("Charset autodetect failed, use {} as charset.", task.getSite().getDefaultCharset());
        }
        return charset;
    }
}
