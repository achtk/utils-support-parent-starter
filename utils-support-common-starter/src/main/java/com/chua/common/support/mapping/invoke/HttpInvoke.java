package com.chua.common.support.mapping.invoke;

import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientBuilder;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpMethod;
import com.chua.common.support.mapping.Request;

/**
 * http调用
 *
 * @author CH
 * @since 2023/09/06
 */
public interface HttpInvoke {


    /**
     * 执行
     *
     * @param url     url
     * @param request 要求
     * @return {@link Object}
     */
    Object execute(String url, Request request);


    public static class DefaultHttpInvoke implements HttpInvoke {
        /**
         * 执行
         *
         * @param url     url
         * @param request 要求
         * @return {@link Object}
         */
        @Override
        public Object execute(String url, Request request) {
            HttpClientBuilder httpClientBuilder = HttpClient.newHttpMethod(HttpMethod.valueOf(request.getMethod()));
            HttpClientInvoker newInvoker = httpClientBuilder.url(url)
                    .body(request.getBody())
                    .header(request.getHeader())
                    .readTimout(request.getReadTimeout())
                    .connectTimout(request.getConnectTimeout())
                    .newInvoker();

            return newInvoker.execute().content();
        }
    }
}
