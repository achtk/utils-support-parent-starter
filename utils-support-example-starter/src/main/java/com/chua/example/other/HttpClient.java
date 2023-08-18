package com.chua.example.other;

/**
 * 客户端
 * @author CH
 */
public interface HttpClient {
    /**
     * get
     * @return get
     */
    static HttpInvokeBuilder get() {
        return new HttpInvokeBuilder(HttpMethod.GET);
    }
}
