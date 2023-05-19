package com.chua.common.support.protocol.server.request;

/**
 * 响应
 *
 * @author CH
 */
public class NullResponse implements Response {

    public static final Response EMPTY = new NullResponse();

    @Override
    public Object getBody() {
        return null;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
