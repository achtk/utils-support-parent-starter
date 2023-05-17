package com.chua.common.support.http;

import com.chua.common.support.spi.ServiceProvider;
import lombok.Data;

/**
 * 可配置构造器
 *
 * @author CH
 */
@Data
public class EnableConfigurationHttpClientBuilder implements HttpClientBuilder {

    private final HttpMethod httpMethod;

    private final HttpRequest.HttpRequestBuilder requestBuilder = HttpRequest.builder();

    private final HttpHeader header = new HttpHeader();


    public EnableConfigurationHttpClientBuilder(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public HttpClientBuilder proxy(String proxy) {
        requestBuilder.proxy(proxy);
        return this;
    }

    @Override
    public HttpClientBuilder dns(String dns) {
        requestBuilder.dns(dns);
        return this;
    }

    @Override
    public HttpClientBuilder maxConnTotal(int maxConnTotal) {
        requestBuilder.maxConnTotal(maxConnTotal);
        return this;
    }

    @Override
    public HttpClientBuilder maxConnPerRoute(int maxConnPerRoute) {
        requestBuilder.maxConnPerRoute(maxConnPerRoute);
        return this;
    }

    @Override
    public HttpClientBuilder client(Object client) {
        requestBuilder.client(client);
        return this;
    }

    @Override
    public HttpClientBuilder ssl(Object sslSocketFactory) {
        requestBuilder.sslSocketFactory(sslSocketFactory);
        return this;
    }

    @Override
    public HttpClientBuilder header(String headerName, String headerValue) {
        header.addHeader(headerName, headerValue);
        return this;
    }

    @Override
    public HttpClientBuilder basicAuth(String basicAuthName, String basicAuthValue) {
        requestBuilder.basicAuth(basicAuthName, basicAuthValue);
        return this;
    }

    @Override
    public HttpClientBuilder body(String bodyName, Object bodyValue) {
        requestBuilder.body(bodyName, bodyValue);
        return this;
    }

    @Override
    public HttpClientBuilder url(String url) {
        this.requestBuilder.url(url);
        return this;
    }

    @Override
    public HttpClientBuilder connectTimout(long timeout) {
        this.requestBuilder.connectTimeout(timeout);
        return this;
    }

    @Override
    public HttpClientBuilder readTimout(long timeout) {
        this.requestBuilder.readTimeout(timeout);
        return this;
    }

    @Override
    public HttpClientBuilder retry(int retry) {
        this.requestBuilder.retry(retry);
        return this;
    }

    @Override
    public HttpClientInvoker newInvoker(String type) {
        return ServiceProvider.of(HttpClientInvoker.class).getNewExtension(type,
                requestBuilder.header(header).build(), httpMethod);
    }
}
