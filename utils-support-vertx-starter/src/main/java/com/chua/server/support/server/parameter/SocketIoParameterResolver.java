package com.chua.server.support.server.parameter;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.describe.describe.ParameterDescribe;
import com.chua.common.support.server.annotations.Header;
import com.chua.common.support.server.annotations.Param;
import com.chua.common.support.server.annotations.Path;
import com.chua.common.support.server.parameter.ParameterResolver;
import com.chua.common.support.server.request.Request;
import com.chua.server.support.server.request.SocketIoRequest;
import com.google.common.base.Strings;

/**
 * socket.io
 * @author CH
 */
public class SocketIoParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(ParameterDescribe describe, Request request) {
        SocketIoRequest ioRequest = (SocketIoRequest) request;
        if(describe.hasAnnotation(Param.class)) {
            Param param = describe.getAnnotationValue(Param.class);
            String value = param.value();
            if(Strings.isNullOrEmpty(value)) {
                return BeanUtils.copyProperties(ioRequest.getData(), describe.returnClassType());
            }

            return ioRequest.getParameter(value);
        }

        if(describe.hasAnnotation(Path.class)) {
            Path path = describe.getAnnotationValue(Path.class);
            return ioRequest.getRequest().getHandshakeData().getUrlParams().get(path.value());
        }

        if(describe.hasAnnotation(Header.class)) {
            Header header = describe.getAnnotationValue(Header.class);
            return ioRequest.getRequest().getHandshakeData().getHttpHeaders().get(header.value());
        }

        return null;
    }

}
