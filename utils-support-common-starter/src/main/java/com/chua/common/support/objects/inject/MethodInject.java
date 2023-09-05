package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

/**
 * 字段注入器
 * @author CH
 */
public interface MethodInject {

    /**
     * 注入
     *
     * @param methodDescribe              字段描述
     * @param bean                        bean
     * @param fieldDescribe               字段描述
     * @param typeDefinitionSourceFactory 类型定义源工厂
     * @return boolean
     * @throws Exception ex
     */
    boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory , FieldDescribe fieldDescribe, MethodDescribe methodDescribe, Object bean) throws Exception;
}
