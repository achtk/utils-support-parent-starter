package com.chua.common.support.protocol.server.parameter;


import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.protocol.server.request.WebsocketRequest;
import com.chua.common.support.utils.StringUtils;

/**
 * websocket
 *
 * @author CH
 */
public class WebSocketParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(ParameterDescribe annotation, Request request) {
        WebsocketRequest websocketRequest = (WebsocketRequest) request;
        if (annotation.hasAnnotation(Param.class)) {
            Param param = annotation.getAnnotation(Param.class);
            if (StringUtils.isNullOrEmpty(param.value())) {
                return websocketRequest.getExt();
            }
            return websocketRequest.getParameter(param.value());
        }

        if (annotation.hasAnnotation(Path.class)) {
            Path path = annotation.getAnnotation(Path.class);
            return websocketRequest.getParameter(path.value());
        }

        return null;
    }

}
