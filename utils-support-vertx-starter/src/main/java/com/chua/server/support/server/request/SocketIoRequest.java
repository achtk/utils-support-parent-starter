package com.chua.server.support.server.request;

import com.chua.common.support.json.Json;
import com.chua.common.support.protocol.server.Constant;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.utils.CollectionUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * socket.io
 * @author CH
 */
public class SocketIoRequest implements Request, Constant {
    @Getter
    private final SocketIOClient request;
    private AckRequest ackSender;
    private final String action;

    @Getter
    private final Map<String, Object> data = new LinkedHashMap<>();

    public SocketIoRequest(SocketIOClient request, String action) {
        this.request = request;
        this.action = action;
    }

    public SocketIoRequest(SocketIOClient request, String action, String data, AckRequest ackSender) {
        this.request = request;
        this.action = action;
        this.data.putAll(Json.fromJson(data, HashMap.class));
        this.ackSender = ackSender;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getParameter(String value) {
        Map<String, List<String>> params = request.getHandshakeData().getUrlParams();
        List<String> userIdList = params.get(value);
        if (!CollectionUtils.isEmpty(userIdList)) {
            return userIdList.get(0);
        }
        return data.getOrDefault(value, "").toString();
    }

    @Override
    public String getBinder(String value) {
        if(HOST.equals(value)) {
            return request.getRemoteAddress().toString();
        }

        return null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return data;
    }

    @Override
    public String getHeader(String value) {
        return getParameter(value);
    }
}
