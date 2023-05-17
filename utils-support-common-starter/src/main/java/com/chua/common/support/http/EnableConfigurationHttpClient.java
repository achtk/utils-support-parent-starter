package com.chua.common.support.http;

/**
 * http客户端
 *
 * @author CH
 */
public class EnableConfigurationHttpClient implements HttpClient {

    @Override
    public HttpClientBuilder newHttpClient(HttpMethod httpMethod) {
        return new EnableConfigurationHttpClientBuilder(httpMethod);
    }
}
