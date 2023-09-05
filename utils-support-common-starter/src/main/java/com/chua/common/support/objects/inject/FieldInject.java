package com.chua.common.support.objects.inject;

import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

/**
 * 字段注入器
 * @author CH
 */
public interface FieldInject {

    /**
     * 注入
     *
     * @param fieldDescribe               字段描述
     * @param bean                        bean
     * @param typeDefinitionSourceFactory 类型定义源工厂
     * @return boolean
     * @throws Exception ex
     */
    boolean inject(TypeDefinitionSourceFactory typeDefinitionSourceFactory, FieldDescribe fieldDescribe, Object bean) throws Exception;
}
