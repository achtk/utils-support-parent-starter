package com.chua.common.support.bean.seeker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * seeker
 *
 * @author CH
 */
public interface Seeker {
    /**
     * 所有类型
     *
     * @return 所有类型
     */
    Set<Class<?>> findAll();

    /**
     * 查询子类
     *
     * @param type 类型
     * @param <E>  类型
     * @return 子类
     */
    <E> Set<Class<? extends E>> getSubTypesOf(Class<E> type);

    /**
     * 查询子类
     *
     * @param type 类型
     * @return 子类
     */
    Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> type);
}
