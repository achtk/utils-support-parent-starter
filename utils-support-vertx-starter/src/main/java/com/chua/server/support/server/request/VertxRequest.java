package com.chua.server.support.server.request;

import com.chua.common.support.protocol.server.Constant;
import com.chua.common.support.protocol.server.request.Request;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求
 * @author CH
 */
public class VertxRequest implements Request, Constant {
    @Getter
    private final HttpServerRequest request;
    private String uri;
    Map<String, Object> rs = new LinkedHashMap<>();

    public VertxRequest(HttpServerRequest request, String uri) {
        this.request = request;
        this.uri = uri;
        MultiMap params = request.params();
        params.forEach(rs::put);
    }

    @Override
    public String getAction() {
        return uri;
    }

    @Override
    public String getParameter(String value) {
        return request.getParam(value);
    }

    @Override
    public String getBinder(String value) {
        if(HOST.equals(value)) {
            return request.host();
        }

        if(PATH.equals(value)) {
            return request.path();
        }

        if(QUERY.equals(value)) {
            return request.query();
        }

        if(URI.equals(value)) {
            return request.uri();
        }

        if(METHOD.equals(value)) {
            return request.method().name();
        }

        if(ACTION.equals(value)) {
            return uri;
        }

        if(VERSION.equals(value)) {
            return request.version().name();
        }

        return null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return rs;
    }

    @Override
    public String getHeader(String value) {
        return request.getHeader(value);
    }
}
