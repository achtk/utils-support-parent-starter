package com.chua.htmlunit.support.spider.downloader;


import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.Request;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Task;
import com.chua.common.support.lang.spider.downloader.AbstractDownloader;
import com.chua.common.support.utils.StringUtils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * htmlunit
 *
 * @author CH
 */
@Slf4j
public class HtmlunitDownloader extends AbstractDownloader {


    static final Map<String, WebClient> webClient = new ConcurrentReferenceHashMap<>(123);

    @Override
    public Page download(Request request, Task task) {
        WebClient webClient1 = getWebClient(task.getSite());
        com.gargoylesoftware.htmlunit.Page webClient1Page = null;
        try {
            webClient1Page = webClient1.getPage(request.getUrl());
        } catch (IOException e) {
            onError(request, task, e);
            log.info("download page {} error", request.getUrl(), e);
            return Page.fail();
        }
        onSuccess(request, task);
        log.info("downloading page success {}", request.getUrl());
        return new Page().setRawText(webClient1Page.toString());
    }

    @Override
    public void setThread(int threadNum) {

    }


    public WebClient getWebClient(Site site) {
        return webClient.computeIfAbsent(site.getDomain(), s -> {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);//关闭css
            webClient.getOptions().setJavaScriptEnabled(true);//开启js
            webClient.getOptions().setRedirectEnabled(true);//重定向
            webClient.getOptions().setThrowExceptionOnScriptError(false);//关闭js报错
            webClient.getOptions().setTimeout(site.getTimeOut());//超时时间
            webClient.getCookieManager().setCookiesEnabled(true);//允许cookie
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
            if (StringUtils.isNotBlank(site.getProxyHost())) {
                ProxyConfig proxyConfig = new ProxyConfig(site.getProxyHost(), site.getProxyPort(), site.getProxyScheme());
                webClient.getOptions().setProxyConfig(proxyConfig);
            }
            Map<String, String> cookies = site.getCookies();
            if (null != cookies) {
                try {
                    cookies.forEach((k, v) -> {
                        try {
                            webClient.addCookie(k + ":" + v, new URL(s), s);
                        } catch (MalformedURLException ignored) {
                        }
                    });
                } catch (Exception ignored) {
                }
            }
            return webClient;
        });

    }
}
