package com.chua.server.support.server.parameter;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.protocol.server.annotations.Header;
import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.parameter.ParameterResolver;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.utils.StringUtils;
import com.chua.server.support.server.request.VertxRequest;

/**
 * vertx
 * @author CH
 */
public class VertxParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(ParameterDescribe describe, Request request) {
        VertxRequest vertxRequest = (VertxRequest) request;
        if(describe.hasAnnotation(Param.class)) {
            Param param = describe.getAnnotationValue(Param.class);
            String value = param.value();
            if(StringUtils.isNullOrEmpty(value)) {
                return BeanUtils.copyProperties(vertxRequest.getParameters(), describe.returnClassType());
            }
            return vertxRequest.getRequest().getParam(value, param.defaultValue());
        }

        if(describe.hasAnnotation(Path.class)) {
            Path path = describe.getAnnotationValue(Path.class);
            return vertxRequest.getRequest().getParam(path.value(), path.defaultValue());
        }

        if(describe.hasAnnotation(Header.class)) {
            Header header = describe.getAnnotationValue(Header.class);
            return vertxRequest.getRequest().getParam(header.value(), header.defaultValue());
        }

        return null;
    }

}
