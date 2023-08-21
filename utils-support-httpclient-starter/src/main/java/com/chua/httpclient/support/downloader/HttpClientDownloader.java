package com.chua.httpclient.support.downloader;

import com.chua.common.support.lang.spider.Page;
import com.chua.common.support.lang.spider.Request;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Task;
import com.chua.common.support.lang.spider.downloader.AbstractDownloader;
import com.chua.common.support.lang.spider.proxy.Proxy;
import com.chua.common.support.lang.spider.proxy.ProxyProvider;
import com.chua.common.support.lang.spider.selector.PlainText;
import com.chua.common.support.lang.spider.utils.CharsetUtils;
import com.chua.common.support.utils.IoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Slf4j
public class HttpClientDownloader extends AbstractDownloader {


    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private ProxyProvider proxyProvider;

    private boolean responseHeader = true;

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(task.getSite());
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(request, task) : null;
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);

            onSuccess(request, task);
            log.info("downloading page success {}", request.getUrl());

            return page;
        } catch (IOException e) {

            onError(request, task, e);
            log.info("download page {} error", request.getUrl(), e);

            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        byte[] bytes = IoUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes, task);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
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
