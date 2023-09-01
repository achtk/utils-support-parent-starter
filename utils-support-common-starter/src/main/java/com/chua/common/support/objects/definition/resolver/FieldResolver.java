package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.definition.element.FieldDefinition;
import com.chua.common.support.utils.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段分解器
 *
 * @author CH
 * @since 2023/09/01
 */
@Spi
public interface FieldResolver {

    /**
     * 收到
     *
     * @param type 类型
     * @return {@link Map}<{@link String}, {@link FieldDefinition}>
     */
    Map<String, FieldDefinition> get(Class<?> type);


    /**
     * 默认注释解析程序
     *
     * @author CH
     * @since 2023/09/01
     */
    @Spi("default")
    class DefaultFieldResolver implements FieldResolver {

        @Override
        public Map<String, FieldDefinition> get(Class<?> type) {
            Field[] fields = type.getDeclaredFields();
            if (ArrayUtils.isEmpty(fields)) {
                return Collections.emptyMap();
            }
            return Arrays.stream(fields).map(it -> new FieldDefinition(it, type)).collect(Collectors.toMap(FieldDefinition::name, it -> it));
        }
    }
}
