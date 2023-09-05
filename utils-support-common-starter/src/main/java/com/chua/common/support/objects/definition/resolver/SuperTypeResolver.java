package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.definition.element.SuperTypeDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 超级分解器
 *
 * @author CH
 * @since 2023/09/01
 */
@Spi
public interface SuperTypeResolver {

    /**
     * 收到
     *
     * @param type 类型
     * @return {@link Map}<{@link String}, {@link FieldDescribe}>
     */
    Map<String, SuperTypeDescribe> get(Class<?> type);


    /**
     * 默认注释解析程序
     *
     * @author CH
     * @since 2023/09/01
     */
    @Spi("default")
    class DefaultSuperTypeResolver implements SuperTypeResolver {

        @Override
        public Map<String, SuperTypeDescribe> get(Class<?> type) {
            Class<?> superclass = type.getSuperclass();
            if (ClassUtils.isObject(superclass)) {
                return Collections.emptyMap();
            }
            return ImmutableBuilder.<String, SuperTypeDescribe>
                    builderOfMap().put(superclass.getTypeName(), new SuperTypeDescribe(superclass, type)).build();
        }
    }
}
