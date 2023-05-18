package com.chua.common.support.context.resolver;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 名称解析器
 * @author CH
 */
@Data
@Builder
@Accessors(chain = true)
public class NamePair {
    /**
     * 注解类型
     */
    private Class<? extends Annotation> annotationType;
    /**
     * 注解类型
     */
    private Annotation annotation;

    /**
     * 名称属性
     */
    @Singular("attribute")
    private Set<String> attribute;
    /**
     * 类型
     */
    private Object type;
}
