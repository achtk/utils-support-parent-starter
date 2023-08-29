package com.chua.common.support.http;

/**
 * http客户端
 *
 * @author CH
 */
public interface HttpClient {
    /**
     * 创建请求
     *
     * @param httpMethod 类型
     * @return 结果
     */
    HttpClientBuilder newHttpClient(HttpMethod httpMethod);

    /**
     * get
     *
     * @param httpMethod method
     * @return get
     */
    static HttpClientBuilder newHttpMethod(HttpMethod httpMethod) {
        return new EnableConfigurationHttpClientBuilder(httpMethod);
    }
    /**
     * get
     *
     * @return get
     */
    default HttpClientBuilder newGet() {
        return newHttpClient(HttpMethod.GET);
    }

    /**
     * POST
     *
     * @return POST
     */
    default HttpClientBuilder newPost() {
        return newHttpClient(HttpMethod.POST);
    }

    /**
     * PUT
     *
     * @return PUT
     */
    default HttpClientBuilder newPut() {
        return newHttpClient(HttpMethod.PUT);
    }

    /**
     * DELETE
     *
     * @return DELETE
     */
    default HttpClientBuilder newDelete() {
        return newHttpClient(HttpMethod.DELETE);
    }

    /**
     * HEADER
     *
     * @return HEADER
     */
    default HttpClientBuilder newHeader() {
        return newHttpClient(HttpMethod.HEAD);
    }

    /**
     * get
     *
     * @return get
     */
    static HttpClientBuilder get() {
        return new EnableConfigurationHttpClientBuilder(HttpMethod.GET);
    }

    /**
     * POST
     *
     * @return POST
     */
    static HttpClientBuilder post() {
        return new EnableConfigurationHttpClientBuilder(HttpMethod.POST);
    }

    /**
     * PUT
     *
     * @return PUT
     */
    static HttpClientBuilder put() {
        return new EnableConfigurationHttpClientBuilder(HttpMethod.PUT);
    }

    /**
     * DELETE
     *
     * @return DELETE
     */
    static HttpClientBuilder delete() {
        return new EnableConfigurationHttpClientBuilder(HttpMethod.DELETE);
    }

    /**
     * HEADER
     *
     * @return HEADER
     */
    static HttpClientBuilder header() {
        return new EnableConfigurationHttpClientBuilder(HttpMethod.HEAD);
    }
}
