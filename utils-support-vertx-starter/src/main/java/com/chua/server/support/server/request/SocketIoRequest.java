package com.chua.server.support.server.request;

import com.chua.common.support.json.Json;
import com.chua.common.support.lang.expression.parser.DelegateExpressionParser;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
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
    final ExpressionParser expressionParser = new DelegateExpressionParser();

    @Getter
    private final Map<String, Object> data = new LinkedHashMap<>();
    private Map<String, Object> param = new LinkedHashMap<>();

    public SocketIoRequest(SocketIOClient request, String action) {
        this(request, action, null, null);
    }

    public SocketIoRequest(SocketIOClient request, String action, String data, AckRequest ackSender) {
        this.request = request;
        this.action = action;
        HashMap hashMap = Json.fromJson(data, HashMap.class);
        if(null != hashMap) {
            this.data.putAll(hashMap);
        }
        this.ackSender = ackSender;
        this.data.put("_ackSender", ackSender);
        this.data.put("_action", action);
        this.data.put("_request", request);
        expressionParser.setVariable(this.data);

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
        return data.getOrDefault(value, expressionParser.parseExpression(value).getStringValue()).toString();
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
