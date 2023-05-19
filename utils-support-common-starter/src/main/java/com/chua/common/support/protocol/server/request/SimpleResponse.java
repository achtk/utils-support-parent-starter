package com.chua.common.support.protocol.server.request;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 响应
 *
 * @author CH
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class SimpleResponse implements Response {

    @NonNull
    private Object object;

    private Throwable throwable;

    @Override
    public Object getBody() {
        return object;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
