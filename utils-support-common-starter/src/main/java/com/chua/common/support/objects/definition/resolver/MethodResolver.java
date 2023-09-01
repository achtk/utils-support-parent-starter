package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.definition.element.MethodDefinition;
import com.chua.common.support.utils.ArrayUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 方法分解器
 *
 * @author CH
 * @since 2023/09/01
 */
@Spi
public interface MethodResolver {

    /**
     * 收到
     *
     * @param type 类型
     * @return {@link List}<{@link MethodDefinition}>
     */
    Map<String, List<MethodDefinition>> get(Class<?> type);

    @Spi("default")
    class DefaultMethodResolver implements MethodResolver {

        @Override
        public Map<String, List<MethodDefinition>> get(Class<?> type) {
            Method[] declaredMethods = type.getDeclaredMethods();
            if (ArrayUtils.isEmpty(declaredMethods)) {
                return Collections.emptyMap();
            }
            Map<String, List<MethodDefinition>> ts = new HashMap<>(declaredMethods.length);
            for (Method method : declaredMethods) {
                ts.computeIfAbsent(method.getName(), it -> new LinkedList<>()).add(new MethodDefinition(method, type));
            }
            return ts;
        }
    }
}
