package com.chua.common.support.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 实体映射
 * @author CH
 */
@Target(ElementType.TYPE)
public @interface MappingAddress {
    /**
     * 远程地址
     * @return 远程地址
     */
    String value();
}
