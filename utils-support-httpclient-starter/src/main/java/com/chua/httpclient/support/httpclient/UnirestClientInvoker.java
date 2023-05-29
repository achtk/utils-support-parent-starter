package com.chua.httpclient.support.httpclient;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.http.HttpMethod;
import com.chua.common.support.http.HttpRequest;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.http.*;
import com.chua.common.support.http.invoke.AbstractHttpClientInvoker;
import com.chua.common.support.http.render.Render;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.http.HttpMethod.*;

/**
 * apache httpclient
 *
 * @author CH
 */
@Slf4j
@Spi(value = "httpclient", order = 2)
public class UnirestClientInvoker extends AbstractHttpClientInvoker {

    private static final Map<HttpRequest, Object> CLIENT_MAP = new ConcurrentHashMap<>();


    public UnirestClientInvoker(HttpRequest request, HttpMethod httpMethod) {
        super(request, httpMethod);
    }


    @Override
    public void execute(ResponseCallback<HttpResponse> responseCallback) {
        if (GET.equals(httpMethod)) {
            executeGetAsync(responseCallback);
        } else if (POST.equals(httpMethod)) {
            executePostAsync(responseCallback);
        } else if (PUT.equals(httpMethod)) {
            executePutAsync(responseCallback);
        } else if (DELETE.equals(httpMethod)) {
            executeDeleteAsync(responseCallback);
        } else if (HEAD.equals(httpMethod)) {
            executeHeadAsync(responseCallback);
        } else if (OPTION.equals(httpMethod)) {
            executeOptionAsync(responseCallback);
        } else if (PATCH.equals(httpMethod)) {
            executePatchAsync(responseCallback);
        }
    }

    private void executePatchAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        HttpRequestWithBody request = Unirest.patch(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);
        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    private void executeOptionAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        GetRequest request = Unirest.options(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);

        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    private void executeHeadAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        GetRequest request = Unirest.head(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        request.queryString(this.request.getBody());
        doAnalysisBasicAuth(request);
        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    private void executeDeleteAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        HttpRequestWithBody request = Unirest.delete(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);

        doAnalysisBasicAuth(request);

        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    private void executePutAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        HttpRequestWithBody request = Unirest.put(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);

        doAnalysisBasicAuth(request);

        request.asBytesAsync(new CallbackFunction(responseCallback));
    }


    @Override
    protected HttpResponse executeDelete() {
        doAnalysisHttpClient();
        HttpRequestWithBody request = Unirest.delete(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);

        doAnalysisBasicAuth(request);

        return doAnalysisResponse(request);
    }

    @Override
    protected HttpResponse executePut() {
        doAnalysisHttpClient();
        HttpRequestWithBody request = Unirest.put(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);

        return doAnalysisResponse(request);
    }

    @Override
    protected HttpResponse executePost() {
        doAnalysisHttpClient();
        HttpRequestWithBody request = Unirest.post(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);

        doAnalysisBasicAuth(request);
        return doAnalysisResponse(request);
    }

    private void executePostAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        HttpRequestWithBody request = Unirest.post(this.url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);

        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    @Override
    protected HttpResponse executeGet() {
        doAnalysisHttpClient();
        GetRequest request = Unirest.get(url);
        doAnalysisHeaders(request);
        doAnalysisRequest(request);
        doAnalysisBasicAuth(request);
        return doAnalysisResponse(request);
    }

    private void doAnalysisHeaders(GetRequest request) {
        request.headers(this.request.getHeader().asSimpleMap());
    }

    private void doAnalysisHeaders(HttpRequestWithBody request) {
        request.headers(this.request.getHeader().asSimpleMap());
    }

    private void executeGetAsync(ResponseCallback<HttpResponse> responseCallback) {
        doAnalysisAsyncHttpClient();
        GetRequest request = Unirest.get(this.request.getUrl());
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);
        request.asBytesAsync(new CallbackFunction(responseCallback));
    }

    @Override
    protected HttpResponse executePatch() {
        doAnalysisHttpClient();
        HttpRequestWithBody request = Unirest.patch(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);
        return doAnalysisResponse(request);
    }

    @Override
    protected HttpResponse executeOption() {
        doAnalysisHttpClient();
        GetRequest request = Unirest.options(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        doAnalysisBasicAuth(request);

        return doAnalysisResponse(request);
    }

    @Override
    protected HttpResponse executeHead() {
        doAnalysisHttpClient();
        GetRequest request = Unirest.head(url);
        doAnalysisRequest(request);
        doAnalysisHeaders(request);
        request.queryString(this.request.getBody());
        doAnalysisBasicAuth(request);
        return doAnalysisResponse(request);
    }

    /**
     * 响应
     *
     * @param request 请求
     * @return 响应
     */
    private HttpResponse doAnalysisResponse(GetRequest request) {
        try {
            return createResponseEntity(request.asBytes());
        } catch (Exception e) {
            return createResponseServerErrorEntity(e);
        }
    }

    /**
     * 响应
     *
     * @param request 请求
     * @return 响应
     */
    private HttpResponse doAnalysisResponse(HttpRequestWithBody request) {
        kong.unirest.HttpResponse<byte[]> response = null;
        try {
            response = request.asBytes();
        } catch (Exception e) {
            return createResponseServerErrorEntity(e);
        }
        return createResponseEntity(response);
    }

    /**
     * 服务器处理异常
     *
     * @param e 异常
     * @return 响应
     */
    private HttpResponse createResponseServerErrorEntity(Exception e) {
        return HttpResponse.builder().code(500).message(e.getLocalizedMessage()).build();
    }

    /**
     * 响应
     *
     * @param response 响应
     * @return 响应
     */
    private HttpResponse createResponseEntity(kong.unirest.HttpResponse<byte[]> response) {
        byte[] content = response.getBody();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();
        builder.content(content);
        builder.code(response.getStatus());
        builder.httpHeader(createHeader(response.getHeaders()));
        if (log.isDebugEnabled()) {
            log.debug("响应状态码: {}", response.getHeaders());
            log.debug("响应数据是否为空: {}", null != content);
        }
        if (log.isTraceEnabled()) {
            log.trace("响应数据: {}", content);
        }
        return builder.build();
    }

    /**
     * 创建消息头
     *
     * @param headers 响应头
     * @return 响应头
     */
    private HttpHeader createHeader(Headers headers) {
        HttpHeader header =
                new HttpHeader();
        List<Header> all = headers.all();
        for (Header header1 : all) {
            header.addHeader(header1.getName(), header1.getValue());
        }
        return header;
    }

    /**
     * basic认证
     *
     * @param request 请求
     */
    private void doAnalysisBasicAuth(GetRequest request) {
        if (!this.request.getBasicAuth().isEmpty()) {
            for (Map.Entry<String, String> entry : this.request.getBasicAuth().entrySet()) {
                request.basicAuth(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * basic认证
     *
     * @param request 请求
     */
    private void doAnalysisBasicAuth(HttpRequestWithBody request) {
        if (!this.request.getBasicAuth().isEmpty()) {
            for (Map.Entry<String, String> entry : this.request.getBasicAuth().entrySet()) {
                request.basicAuth(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 设置请求参数
     *
     * @param request 请求
     */
    private void doAnalysisRequest(GetRequest request) {
        request.queryString(recombine(this.request.getBody()));
    }

    /**
     * 设置请求参数
     *
     * @param request 请求
     */
    private void doAnalysisRequest(HttpRequestWithBody request) {
        doAnalysisRequest(this.httpMethod.name());

        if (!this.request.getHeader().isEmpty()) {
            request.headers(this.request.getHeader().asSimpleMap());
        }

        if (StringUtils.isNotBlank(this.request.getBodyStr())) {
            request.body(this.request.getBodyStr());
            return;
        }

        //上传文件
        if (this.request.isFormData() && this.request.hasBin()) {
            this.request.getBody().forEach(request::field);
            return;
        }

        String contentType = CollectionUtils.findFirst(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), "*");
        Render render = ServiceProvider.of(com.chua.common.support.http.render.Render.class).getNewExtension(contentType);
        byte[] bytes = render.render(this.request.getBody(), contentType);
        request.body(bytes);
    }

    /**
     * 请求信息
     *
     * @param method 方法
     */
    private void doAnalysisRequest(String method) {
        if (log.isDebugEnabled()) {
            log.debug("请求信息: {}", method);
        }
    }

    /**
     * 重组请求
     *
     * @param body 请求
     * @return 请求
     */
    private Map<String, Object> recombine(Map<String, Object> body) {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                continue;
            }
            if (value instanceof Iterable) {
                body.put(entry.getKey(), Joiner.on(",").join((Iterable<?>) value));
            }
            if (value instanceof Map) {
                body.put(entry.getKey(), Joiner.on("&").withKeyValueSeparator("=").join((Map<?, ?>) value));
            }
        }
        return body;
    }

    /**
     * 设置客户端
     */
    private void doAnalysisHttpClient() {
        Config config = Unirest.config();
        config.httpClient(getClient());
    }

    /**
     * 设置客户端
     */
    private void doAnalysisAsyncHttpClient() {
        Config config = Unirest.config();
        config.asyncClient(getAsyncClient());
    }


    /**
     * 获取客户端
     *
     * @return 客户端
     */
    protected CloseableHttpAsyncClient getAsyncClient() {
        if (CLIENT_MAP.containsKey(request)) {
            return (CloseableHttpAsyncClient) CLIENT_MAP.get(request);
        }
        CloseableHttpAsyncClient client = (CloseableHttpAsyncClient) request.getClient();
        client = null == client ? getCustomAsyncClient() : client;
        CLIENT_MAP.put(request, client);
        return client;
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    protected CloseableHttpClient getClient() {
        if (CLIENT_MAP.containsKey(request)) {
            return (CloseableHttpClient) CLIENT_MAP.get(request);
        }
        CloseableHttpClient client = (CloseableHttpClient) request.getClient();
        client = null == client ? getCustomClient() : client;
        CLIENT_MAP.put(request, client);
        return client;
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    private CloseableHttpAsyncClient getCustomAsyncClient() {
        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClients.custom();

        //对照UA字串的标准格式理解一下每部分的意思
        httpAsyncClientBuilder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");

        if (this.request.getMaxConnTotal() > 0) {
            httpAsyncClientBuilder.setMaxConnTotal(this.request.getMaxConnTotal());
        }

        if (this.request.getMaxConnPerRoute() > 0) {
            httpAsyncClientBuilder.setMaxConnPerRoute(this.request.getMaxConnPerRoute());
        }

        RequestConfig.Builder builder = RequestConfig.custom();
        if (this.request.getConnectTimeout() > 0) {
            //连接超时,连接建立时间,三次握手完成时间
            builder.setConnectTimeout((int) this.request.getConnectTimeout());
        }


        if (this.request.getReadTimeout() > 0) {
            //连接超时,连接建立时间,三次握手完成时间
            builder.setConnectionRequestTimeout((int) this.request.getReadTimeout());
        }

        if (this.request.getConnectTimeout() > 0) {
            //连接超时,连接建立时间,三次握手完成时间
            builder.setSocketTimeout((int) this.request.getConnectTimeout());
        }

        RequestConfig config = builder.build();

        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        //设置连接池大小
        ConnectingIOReactor ioReactor;
        PoolingNHttpClientConnectionManager connManager = null;

        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            connManager = new PoolingNHttpClientConnectionManager(ioReactor, registry());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == connManager) {
            return null;
        }

        if (this.request.getMaxConnPerRoute() > 0) {
            //per route最大连接数设置
            connManager.setDefaultMaxPerRoute(this.request.getMaxConnPerRoute());
        }

        if (this.request.getMaxConnTotal() > 0) {
            //最大连接数设置1
            connManager.setMaxTotal(this.request.getMaxConnTotal());
        }


        httpAsyncClientBuilder.setConnectionManager(connManager);
        httpAsyncClientBuilder.setDefaultRequestConfig(config);

        CloseableHttpAsyncClient client;
        if (!isHttps) {
            client = httpAsyncClientBuilder.build();
        } else {
            client = httpAsyncClientBuilder.setSSLContext(getSslContext()).build();
        }
        client.start();

        return client;
    }


    /**
     * Registry
     *
     * @return
     */
    @SuppressWarnings("ALL")
    public static Registry<SchemeIOSessionStrategy> registry() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustManager(), null);
        SSLIOSessionStrategy sslioSessionStrategy = new SSLIOSessionStrategy(sslContext, new NoopHostnameVerifier());
        return RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", sslioSessionStrategy)
                .build();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    private CloseableHttpClient getCustomClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        //对照UA字串的标准格式理解一下每部分的意思
        httpClientBuilder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");

        if (request.getRetry() > 0) {
            httpClientBuilder.setRetryHandler(new StandardHttpRequestRetryHandler(request.getRetry(), false));
        }

        if (!StringUtils.isNullOrEmpty(request.getDns())) {
            httpClientBuilder.setDnsResolver(host -> InetAddress.getAllByName(request.getDns()));
        }

        if (request.getMaxConnTotal() > 0) {
            httpClientBuilder.setMaxConnTotal(request.getMaxConnTotal());
        }

        if (request.getMaxConnPerRoute() > 0) {
            httpClientBuilder.setMaxConnPerRoute(request.getMaxConnPerRoute());
        }

        RequestConfig.Builder builder = RequestConfig.custom();
        if (request.getConnectTimeout() > 0) {
            builder.setConnectTimeout((int) request.getConnectTimeout());
        }

        if (request.getReadTimeout() > 0) {
            builder.setConnectionRequestTimeout((int) request.getReadTimeout());
        }

        if (request.getConnectTimeout() > 0) {
            builder.setSocketTimeout((int) request.getConnectTimeout());
        }

        RequestConfig config = builder.build();
        HttpClientConnectionManager connManager = null;
        try {
            connManager = new PoolingHttpClientConnectionManager();
        } catch (Exception ignored) {
        }

        httpClientBuilder.setConnectionManager(connManager);
        httpClientBuilder.setDefaultRequestConfig(config);
        if (request.getMaxConnTotal() > 0) {
            httpClientBuilder.setMaxConnPerRoute(request.getMaxConnTotal());
        }

        if (request.getMaxConnTotal() > 0) {
            httpClientBuilder.setMaxConnTotal(request.getMaxConnTotal());
        }

        if (!isHttps) {
            return httpClientBuilder.build();
        }

        try {
            return httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory()).build();
        } catch (Exception ignored) {
        }
        return httpClientBuilder.build();
    }

    /**
     * Registry
     *
     * @return SSLConnectionSocketFactory
     */
    public static SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustManager(), null);
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    /**
     * 获取sslContext
     *
     * @return SSLContext
     */
    private SSLContext getSslContext() {
        if (isHttps) {
            if (null == request.getSslSocketFactory()) {
                try {
                    return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

                } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                    e.printStackTrace();
                }
            } else {
                Object sslSocketFactory = request.getSslSocketFactory();
                return sslSocketFactory instanceof SSLContext ? (SSLContext) sslSocketFactory : null;
            }
        }
        return null;
    }

    /**
     * TrustManager[]
     *
     * @return TrustManager
     */
    public static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
    }


    private class CallbackFunction implements Callback<byte[]> {
        private ResponseCallback responseCallback;

        public CallbackFunction(ResponseCallback responseCallback) {
            this.responseCallback = responseCallback;
        }

        @Override
        public void completed(kong.unirest.HttpResponse<byte[]> response) {
            responseCallback.onResponse(createResponseEntity(response));
        }

        @Override
        public void failed(UnirestException e) {
            responseCallback.onResponse(createResponseServerErrorEntity(e));
            responseCallback.onFailure(e);
        }

        @Override
        public void cancelled() {

        }
    }


}
