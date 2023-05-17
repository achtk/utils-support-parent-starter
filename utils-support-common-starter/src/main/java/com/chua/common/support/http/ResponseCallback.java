package com.chua.common.support.http;


/**
 * 异步回调
 *
 * @author admin
 */
public interface ResponseCallback<T> {
    /**
     * 异常
     *
     * @param e 异常
     */
    void onFailure(Throwable e);

    /**
     * 成功
     *
     * @param response 结果
     */
    void onResponse(T response);
}
