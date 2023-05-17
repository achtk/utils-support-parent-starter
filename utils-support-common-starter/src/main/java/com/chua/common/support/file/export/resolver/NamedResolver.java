package com.chua.common.support.file.export.resolver;


import com.chua.common.support.value.Pair;

import java.lang.reflect.Field;

/**
 * 名称解析
 *
 * @author CH
 */
public interface NamedResolver {

    /**
     * 名称解析器
     *
     * @param field 字段
     * @return 名称
     */
    Pair name(Field field);
}
