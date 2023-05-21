package com.chua.common.support.protocol.server.request;

import java.util.Map;

/**
 * 请求
 *
 * @author CH
 */
public class DelegateRequest implements Request {

    @Override
    public String getAction() {
        return null;
    }

    @Override
    public String getParameter(String value) {
        return null;
    }

    @Override
    public String getBinder(String value) {
        return null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getHeader(String value) {
        return null;
    }
}
