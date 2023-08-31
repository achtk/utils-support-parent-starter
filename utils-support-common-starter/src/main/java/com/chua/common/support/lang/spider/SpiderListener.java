package com.chua.common.support.lang.spider;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SpiderListener {
    /**
     * 成功回调
     *
     * @param request 请求
     */
    void onSuccess(Request request);

    /**
     * 失败回调
     *
     * @param request 回调
     * @deprecated Use {@link #onError(Request, Exception)} instead.
     */
    @Deprecated
    default void onError(Request request) {
    }

    /**
     * 失败回调
     *
     * @param request 回调
     * @param e       异常
     */
    default void onError(Request request, Exception e) {
        this.onError(request);
    }

}
