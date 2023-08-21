package com.chua.common.support.lang.spide.listener;

import com.chua.common.support.lang.spide.request.Request;

/**
 * 监听
 * @author CH
 */
public interface Listener {


    void onSuccess(Request request);

    /**
     * @deprecated Use {@link #onError(Request, Exception)} instead.
     */
    @Deprecated
    default void onError(Request request) {
    }

    default void onError(Request request, Exception e) {
        this.onError(request);
    }
}
