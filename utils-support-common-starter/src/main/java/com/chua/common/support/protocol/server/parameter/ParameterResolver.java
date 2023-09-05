package com.chua.common.support.protocol.server.parameter;


import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.protocol.server.annotations.Header;
import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.request.Request;

/**
 * 参数解析器
 *
 * @author CH
 */
public interface ParameterResolver {
    /**
     * 决定
     * 解析参数
     *
     * @param parameterDefinition 描述
     * @param request  要求
     * @return 结果
     */
    Object resolve(ParameterDescribe parameterDefinition, Request request);

    /**
     * 是否匹配
     *
     * @param parameterDefinition 描述
     * @return 是否匹配
     */
    default boolean isMatch(ParameterDescribe parameterDefinition) {
        return parameterDefinition.hasAnnotation(Param.class) ||
                parameterDefinition.hasAnnotation(Header.class) ||
                parameterDefinition.hasAnnotation(Path.class);
    }
}
