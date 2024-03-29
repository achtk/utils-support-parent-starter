package com.chua.common.support.reflection.reflections.util;

import com.chua.common.support.reflection.reflections.Store;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sam function for store query {@code apply(C) -> Set<T>}
 * <pre>{@code QueryFunction<T> query = ctx -> ctx.get(key) }</pre>
 * <p>supports functional composition {@link #filter(Predicate)}, {@link #map(Function)}, {@link #flatMap(Function)}, ...
 *
 * @author Administrator
 */
public interface QueryFunction<C, T> extends Function<C, Set<T>>, NameHelper {
    /**
     * 执行
     *
     * @param ctx ctx
     * @return Set
     */
    @Override
    Set<T> apply(C ctx);

    /**
     * empty
     *
     * @return QueryFunction
     */
    static <C, T> QueryFunction<Store, T> empty() {
        return ctx -> Collections.emptySet();
    }

    /**
     * single
     *
     * @param element 元素
     * @return QueryFunction
     */
    static <C, T> QueryFunction<Store, T> single(T element) {
        return ctx -> Collections.singleton(element);
    }

    /**
     * set
     *
     * @param elements 元素
     * @return QueryFunction
     */
    static <C, T> QueryFunction<Store, T> set(Collection<T> elements) {
        return ctx -> new LinkedHashSet<>(elements);
    }

    /**
     * filter by predicate <pre>{@code SubTypes.of(type).filter(withPrefix("org"))}</pre>
     *
     * @param predicate predicate
     * @return QueryFunction
     */
    default QueryFunction<C, T> filter(Predicate<? super T> predicate) {
        return ctx -> apply(ctx).stream().filter(predicate).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * map by function <pre>{@code TypesAnnotated.with(annotation).asClass().map(Annotation::annotationType)}</pre>
     *
     * @param function function
     * @return QueryFunction
     */
    default <R> QueryFunction<C, R> map(Function<? super T, ? extends R> function) {
        return ctx -> apply(ctx).stream().map(function).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * flatmap by function <pre>{@code QueryFunction<Method> methods = SubTypes.of(type).asClass().flatMap(Methods::of)}</pre>
     *
     * @param function function
     * @return QueryFunction
     */
    default <R> QueryFunction<C, R> flatMap(Function<T, ? extends Function<C, Set<R>>> function) {
        return ctx -> apply(ctx).stream().flatMap(t -> function.apply(t).apply(ctx).stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * transitively get all by {@code builder} <pre>{@code SuperTypes.of(type).getAll(Annotations::get)}</pre>
     *
     * @param builder function
     * @return QueryFunction
     */
    default QueryFunction<C, T> getAll(Function<T, QueryFunction<C, T>> builder) {
        return getAll(builder, t -> t);
    }

    /**
     * transitively get all by {@code builder} <pre>{@code SuperTypes.of(type).getAll(Annotations::get)}</pre>
     *
     * @param builder  function
     * @param traverse function
     * @return QueryFunction
     */
    default <R> QueryFunction<C, R> getAll(Function<T, QueryFunction<C, R>> builder, Function<R, T> traverse) {
        return ctx -> {
            List<T> workKeys = new ArrayList<>(apply(ctx));
            Set<R> result = new LinkedHashSet<>();
            for (int i = 0; i < workKeys.size(); i++) {
                T key = workKeys.get(i);
                Set<R> apply = builder.apply(key).apply(ctx);
                for (R r : apply) {
                    if (result.add(r)) {
                        workKeys.add(traverse.apply(r));
                    }
                }
            }
            return result;
        };
    }

    /**
     * concat elements from function <pre>{@code Annotations.of(method).add(Annotations.of(type))}</pre>
     *
     * @param function 回調
     * @return QueryFunction
     */
    default <R> QueryFunction<C, T> add(QueryFunction<C, T> function) {
        return ctx -> Stream.of(apply(ctx), function.apply(ctx))
                .flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * convert to given {@code type}, uses {@link NameHelper#forName(String, Class, ClassLoader...)}
     * <pre>{@code Methods.of(type).as(Method.class)}</pre>
     * @param type 类型
     * @param loaders 加载器
     * @return QueryFunction
     */
    default <R> QueryFunction<C, R> as(Class<? extends R> type, ClassLoader... loaders) {
        return ctx -> {
            Set<T> apply = apply(ctx);
            //noinspection unchecked
            return (Set<R>) apply.stream().findFirst().map(first ->
                    type.isAssignableFrom(first.getClass()) ? apply :
                            first instanceof String ? ((Set<R>) forNames((Collection<String>) apply, type, loaders)) :
                                    first instanceof AnnotatedElement ? ((Set<R>) forNames(toNames((Collection<AnnotatedElement>) apply), type, loaders)) :
                                            apply.stream().map(t -> (R) t).collect(Collectors.toCollection(LinkedHashSet::new))
            ).orElse(apply);
        };
    }

    /**
     * convert elements to {@code Class} using {@link NameHelper#forName(String, Class, ClassLoader...)}
     * <pre>{@code SubTypes.of(type).asClass()}</pre>
     * @param loaders 加载器
     * @return QueryFunction
     */
    default <R> QueryFunction<C, Class<?>> asClass(ClassLoader... loaders) {
        // noinspection unchecked
        return ctx -> ((Set<Class<?>>) forNames((Set) apply(ctx), Class.class, loaders));
    }

    /**
     * convert elements to String using {@link NameHelper#toName(AnnotatedElement)}
     * @return QueryFunction
     */
    default QueryFunction<C, String> asString() {
        return ctx -> new LinkedHashSet<>(toNames((AnnotatedElement) apply(ctx)));
    }

    /**
     * cast elements as {@code <R>} unsafe
     * @return QueryFunction
     */
    default <R> QueryFunction<C, Class<? extends R>> as() {
        return ctx -> new LinkedHashSet<>((Set<? extends Class<R>>) apply(ctx));
    }
}
