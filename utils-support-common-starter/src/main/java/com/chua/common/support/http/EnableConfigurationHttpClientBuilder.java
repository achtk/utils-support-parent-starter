package com.chua.common.support.http;

import com.chua.common.support.placeholder.MapMixSystemPlaceholderResolver;
import com.chua.common.support.placeholder.PlaceholderResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.DynamicValue;
import com.chua.common.support.value.MapDynamicValue;
import com.chua.common.support.value.Value;
import lombok.Data;

import java.util.Map;
import java.util.function.Function;

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
    public HttpClientBuilder body(String body) {
        requestBuilder.bodyStr(body);
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
        HttpRequest request = requestBuilder.header(header).build();
        Map<String, Object> body = request.getBody();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            if(entry.getValue() instanceof Value) {
                body.put(entry.getKey(), createValue(body, (Value)entry.getValue()));
            }
        }
        return ServiceProvider.of(HttpClientInvoker.class).getNewExtension(type,
                request, httpMethod);
    }

    @SuppressWarnings("ALL")
    public static Object createValue(Map<String, Object> body, Value value) {
        PlaceholderResolver placeholderResolver = new MapMixSystemPlaceholderResolver(body);
        if(value instanceof DynamicValue) {
            Function function = ((MapDynamicValue) value).getFunction();
            return placeholderResolver.resolvePlaceholder(function.apply(body).toString());
        }
        return placeholderResolver.resolvePlaceholder(value.getStringValue());
    }
}
