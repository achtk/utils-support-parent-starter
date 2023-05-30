package com.chua.common.support.mapping.builder;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.json.jsonpath.JsonPath;
import com.chua.common.support.mapping.annotation.MappingResponse;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 请求
 * @author CH
 */
@Data
public class Response {

    private final Method method;
    private final boolean isList;
    private final MappingResponse mappingResponse;
    private final Class<?> returnType;
    private String jsonPathExpression;
    private Class<?> target;
    private Object content;
    private final Request request;

    public Response(Object content, Request request) {
        this.content = content;
        if(content instanceof byte[]) {
            this.content = new String((byte[]) content);
        }
        this.request = request;
        this.method = request.getMethod();
        this.returnType = ClassUtils.getActualType(method.getReturnType());
        this.mappingResponse = request.getMappingResponse();
        this.target = returnType;
        this.isList = Collection.class.isAssignableFrom(returnType);
        if(null != mappingResponse) {
            Class<?> target1 = mappingResponse.target();
            if(!ClassUtils.isVoid(target1)) {
                this.target = target1;
            }
            this.jsonPathExpression = mappingResponse.value();
        }
    }

    /**
     * 获取结果
     * @return 获取结果
     */
    public Object getValue() {
        Object content1 = content;
        if(!StringUtils.isNullOrEmpty(jsonPathExpression) ) {
            if(content1 instanceof String) {
                content1 = JsonPath.parse(content1.toString()).read(jsonPathExpression);
            } else if(content1 instanceof InputStream) {
                content1 = JsonPath.parse((InputStream)content1).read(jsonPathExpression);
            }
        }

        Object rs = null;
        if(isList) {
            try {
                if (List.class == returnType) {
                    rs = BeanUtils.copyPropertiesList((List<?>) content1, target);
                } else if (Set.class == returnType) {
                    rs = BeanUtils.copyPropertiesSet((Set<?>) content1, target);
                }

                return filter(rs);
            } catch (Exception ignored) {
            }
            return Collections.emptyList();
        }

        return filter(BeanUtils.copyProperties(content1, target));
    }

    private Object filter(Object rs) {
        request.withFilter(rs);
        return rs;
    }
}
