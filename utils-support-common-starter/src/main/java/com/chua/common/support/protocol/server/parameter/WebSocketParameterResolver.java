package com.chua.common.support.protocol.server.parameter;


import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.protocol.server.request.WebsocketRequest;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.utils.StringUtils;

/**
 * websocket
 *
 * @author CH
 */
public class WebSocketParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(ParameterDescribe describe, Request request) {
        WebsocketRequest websocketRequest = (WebsocketRequest) request;
        if (describe.hasAnnotation(Param.class)) {
            Param param = describe.getAnnotationValue(Param.class);
            if (StringUtils.isNullOrEmpty(param.value())) {
                return websocketRequest.getExt();
            }
            return websocketRequest.getParameter(param.value());
        }

        if (describe.hasAnnotation(Path.class)) {
            Path path = describe.getAnnotationValue(Path.class);
            return websocketRequest.getParameter(path.value());
        }

        return null;
    }

}
