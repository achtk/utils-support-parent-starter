package com.chua.common.support.function;

import java.lang.reflect.Method;

/**
 * MethodFilter
 *
 * @author CH
 */
public interface MethodFilter {
    public static final MethodFilter USER_DECLARED_METHODS =
            (method -> !method.isBridge() && !method.isSynthetic() && (method.getDeclaringClass() != Object.class));

    /**
     * 是否匹配
     *
     * @param method method
     * @return 是否匹配
     */
    boolean matches(Method method);

    /**
     * Create a composite filter based on this filter <em>and</em> the provided filter.
     * <p>If this filter does not match, the next filter will not be applied.
     *
     * @param next the next {@code MethodFilter}
     * @return a composite {@code MethodFilter}
     * @throws IllegalArgumentException if the MethodFilter argument is {@code null}
     * @since 5.3.2
     */
    default MethodFilter and(MethodFilter next) {
        return method -> matches(method) && next.matches(method);
    }
}
