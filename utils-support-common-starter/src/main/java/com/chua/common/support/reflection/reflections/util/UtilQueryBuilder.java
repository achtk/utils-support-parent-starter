package com.chua.common.support.reflection.reflections.util;

import com.chua.common.support.reflection.reflections.ReflectionUtils;
import com.chua.common.support.reflection.reflections.Store;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * query builder for {@link QueryFunction}
 * <pre>{@code UtilQueryBuilder<Annotation> builder =
 *   element -> store -> element.getDeclaredAnnotations()} </pre>
 *
 * @author Administrator
 */
public interface UtilQueryBuilder<F, E> {
    /**
     * 得到
     * get direct values of given element
     *
     * @param element 元素
     * @return {@link QueryFunction}<{@link Store}, {@link E}>
     */
    QueryFunction<Store, E> get(F element);

    /**
     * get transitive values of given element
     *
     * @param element 元素
     * @return {@link QueryFunction}<{@link Store}, {@link E}>
     */
    default QueryFunction<Store, E> of(final F element) {
        return of(ReflectionUtils.<Class<?>>extendType().get((AnnotatedElement) element));
    }

    /**
     * get transitive value of given element filtered by predicate
     *
     * @param element   元素
     * @param predicate 谓词
     * @return {@link QueryFunction}<{@link Store}, {@link E}>
     */
    default QueryFunction<Store, E> of(final F element, Predicate<? super E> predicate) {
        return of(element).filter(predicate);
    }

    /**
     * compose given function
     *
     * @param function 函数
     * @return {@link QueryFunction}<{@link Store}, {@link E}>
     */
    default <T> QueryFunction<Store, E> of(QueryFunction<Store, T> function) {
        return store -> function.apply(store).stream()
                .flatMap(t -> get((F) t).apply(store).stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
