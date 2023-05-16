package com.chua.common.support.context.resolver;


import com.chua.common.support.context.enums.Scope;

/**
 * 作用范围解析器
 *
 * @author CH
 */
public interface ScopeResolver {
    /**
     * 作用范围
     *
     * @param type 类型
     * @return 作用范围
     */
    Scope scope(Class<?> type);
}
