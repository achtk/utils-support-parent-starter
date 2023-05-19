package com.chua.common.support.protocol.server.request;

import com.chua.common.support.protocol.websocket.WebSocket;
import com.chua.common.support.protocol.websocket.handshake.ClientHandshake;
import lombok.Getter;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * request
 *
 * @author CH
 */
public class WebsocketRequest implements Request {
    @Getter
    private final WebSocket request;
    @Getter
    private final ClientHandshake handshake;
    private final String action;
    @Getter
    private final Object ext;
    private Map<String, Object> param = new LinkedHashMap<>();


    public WebsocketRequest(WebSocket request, ClientHandshake handshake, String action, Object ext) {
        this(request, handshake, action, ext, Collections.emptyMap());

    }

    public WebsocketRequest(WebSocket request, ClientHandshake handshake, String action, Object ext, Map<String, String> param) {
        this.request = request;
        this.handshake = handshake;
        this.action = action;
        this.ext = ext;
        if (null != param) {
            this.param.putAll(param);
        }

        if (null != handshake) {
            Iterator<String> stringIterator = handshake.iterateHttpFields();
            while (stringIterator.hasNext()) {
                String key = stringIterator.next();
                this.param.put(key, handshake.getFieldValue(key));
            }
        }

        this.param.put("_source", ext);
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getParameter(String value) {
        String rs = null;
        if (param.containsKey(value)) {
            rs = param.get(value) + "";
        }

        return rs;
    }

    @Override
    public String getBinder(String value) {
        return null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return param;
    }

    @Override
    public String getHeader(String value) {
        return null;
    }
}
