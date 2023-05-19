package com.chua.common.support.protocol.server.parameter;


import com.chua.common.support.protocol.server.annotations.Header;
import com.chua.common.support.protocol.server.annotations.Param;
import com.chua.common.support.protocol.server.annotations.Path;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 参数解析器
 *
 * @author CH
 */
public interface ParameterResolver {
    /**
     * 解析参数
     *
     * @param describe 描述
     * @param request
     * @return 结果
     */
    Object resolve(ParameterDescribe describe, Request request);

    /**
     * 是否匹配
     *
     * @param describe 描述
     * @return 是否匹配
     */
    default boolean isMatch(ParameterDescribe describe) {
        return describe.hasAnnotation(Param.class) ||
                describe.hasAnnotation(Header.class) ||
                describe.hasAnnotation(Path.class);
    }
}
