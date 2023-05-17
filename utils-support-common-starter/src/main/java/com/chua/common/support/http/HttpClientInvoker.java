package com.chua.common.support.http;

import com.chua.common.support.annotations.Spi;

/**
 * invoke
 *
 * @author CH
 */
@Spi("httpclient")
public interface HttpClientInvoker {
    /**
     * 执行
     *
     * @return 结果
     */
    HttpResponse execute();

    /**
     * 执行
     *
     * @param consumer 回调
     */
    void execute(ResponseCallback<HttpResponse> consumer);

}
