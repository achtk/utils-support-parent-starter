package com.chua.server.support.server.parameter;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.protocol.server.annotations.Header;
import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.parameter.ParameterResolver;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.utils.StringUtils;
import com.chua.server.support.server.request.SocketIoRequest;

/**
 * socket.io
 * @author CH
 */
public class SocketIoParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(ParameterDescribe describe, Request request) {
        SocketIoRequest ioRequest = (SocketIoRequest) request;
        if(describe.hasAnnotation(Param.class)) {
            Param param = describe.getAnnotation(Param.class);
            String value = param.value();
            if(StringUtils.isNullOrEmpty(value)) {
                return BeanUtils.copyProperties(ioRequest.getData(), describe.getType());
            }

            return ioRequest.getParameter(value);
        }

        if(describe.hasAnnotation(Path.class)) {
            Path path = describe.getAnnotation(Path.class);
            return ioRequest.getRequest().getHandshakeData().getUrlParams().get(path.value());
        }

        if(describe.hasAnnotation(Header.class)) {
            Header header = describe.getAnnotation(Header.class);
            return ioRequest.getRequest().getHandshakeData().getHttpHeaders().get(header.value());
        }

        return null;
    }

}
