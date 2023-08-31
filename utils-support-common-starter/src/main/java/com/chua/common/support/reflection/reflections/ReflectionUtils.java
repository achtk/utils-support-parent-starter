package com.chua.common.support.reflection.reflections;

import com.chua.common.support.reflection.reflections.util.AbstractClasspathHelper;
import com.chua.common.support.reflection.reflections.util.QueryFunction;
import com.chua.common.support.reflection.reflections.util.ReflectionUtilsPredicates;
import com.chua.common.support.reflection.reflections.util.UtilQueryBuilder;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * utils for querying java reflection meta types
 * <p>see {@link #SUPER_TYPES}, {@link #ANNOTATIONS}, {@link #ANNOTATION_TYPES}, {@link #METHODS}, {@link #CONSTRUCTORS} and {@link #FIELDS}.
 * <pre>{@code
 * Set<Class<?>> supertypes = get(SuperTypes.of(type))
 * Set<Annotation> annotations = get(Annotations.of(type))
 * }</pre>
 * <p>generally, apply {@link #get(QueryFunction)} on {@link QueryFunction} created by {@link UtilQueryBuilder}, and optionally use the functional methods in QueryFunction.
 * <pre>{@code get(Methods.of(type)
 *   .filter(withPublic().and(withPrefix("get")).and(withParameterCount(0)))
 *   .as(Method.class)
 *   .map(m -> ...))
 * }</pre>
 * <p>or (previously), use {@code getAllXXX(type/s, withYYY)} methods:
 * <pre>{@code getAllSuperTypes(), getAllFields(), getAllMethods(), getAllConstructors() }
 * </pre>
 * <p>
 * some predicates included here:
 * <ul>
 * <li>{@link #withPublic()}
 * <li>{@link #withParametersCount(int)}}
 * <li>{@link #withAnnotation(Annotation)}
 * <li>{@link #withParameters(Class[])}
 * <li>{@link #withModifier(int)}
 * <li>{@link #withReturnType(Class)}
 * </ul>
 * <pre>{@code
 * import static com.chua.common.support.reflections.ReflectionUtils.*;
 *
 * Set<Method> getters =
 *     get(Methods(classes)
 *     .filter(withModifier(Modifier.PUBLIC).and(withPrefix("get")).and(withParametersCount(0)));
 *
 * get(Annotations.of(method)
 *   .filter(withAnnotation())
 *   .map(annotation -> Methods.of(annotation)
 *     .map(method -> )))))
 *   .stream()...
 * }</pre>
 *  @author Administrator
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReflectionUtils extends ReflectionUtilsPredicates {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(8);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
    }

    /**
     * get type elements {@code <T>} by applying {@link QueryFunction} <pre>{@code get(SuperTypes.of(type))}</pre>
     */
    public static <C, T> Set<T> get(QueryFunction<C, T> function) {
        return function.apply(null);
    }

    /**
     * get type elements {@code <T>} by applying {@link QueryFunction} and {@code predicates}
     */
    public static <T> Set<T> get(QueryFunction<Store, T> queryFunction, Predicate<? super T>... predicates) {
        return get(queryFunction.filter(Arrays.stream((Predicate[]) predicates).reduce(t -> true, Predicate::and)));
    }

    private static final List<String> OBJECT_METHOD_NAMES =
            Arrays.asList("equals", "hashCode", "toString", "wait", "notify", "notifyAll");

    /**
     * predicate to filter out {@code Object} methods
     */
    public static final Predicate<Method> NOT_OBJECT_METHOD = m -> !OBJECT_METHOD_NAMES.contains(m.getName());

    /**
     * query super class <pre>{@code get(SuperClass.of(element)) -> Set<Class<?>>}</pre>
     * <p>see also {@link ReflectionUtils#SUPER_TYPES}, {@link ReflectionUtils#INTERFACES}
     */
    public static final UtilQueryBuilder<Class<?>, Class<?>> SUPER_CLASS =
            element -> ctx -> {
                Class<?> superclass = element.getSuperclass();
                return superclass != null && !superclass.equals(Object.class) ? Collections.singleton(superclass) : Collections.emptySet();
            };

    /**
     * query interfaces <pre>{@code get(Interfaces.of(element)) -> Set<Class<?>>}</pre>
     */
    public static final UtilQueryBuilder<Class<?>, Class<?>> INTERFACES =
            element -> ctx -> Stream.of(element.getInterfaces()).collect(Collectors.toCollection(LinkedHashSet::new));

    /**
     * query super classes and interfaces including element <pre>{@code get(SuperTypes.of(element)) -> Set<Class<?>> }</pre>
     */
    public static final UtilQueryBuilder<Class<?>, Class<?>> SUPER_TYPES =
            new UtilQueryBuilder<Class<?>, Class<?>>() {
                @Override
                public QueryFunction<Store, Class<?>> get(Class<?> element) {
                    return SUPER_CLASS.get(element).add(INTERFACES.get(element));
                }

                @Override
                public QueryFunction<Store, Class<?>> of(Class<?> element) {
                    return QueryFunction.<Store, Class<?>>single(element).getAll(SUPER_TYPES::get);
                }
            };

    /**
     * query annotations <pre>{@code get(Annotation.of(element)) -> Set<Annotation> }</pre>
     */
    public static final UtilQueryBuilder<AnnotatedElement, Annotation> ANNOTATIONS =
            new UtilQueryBuilder<AnnotatedElement, Annotation>() {
                @Override
                public QueryFunction<Store, Annotation> get(AnnotatedElement element) {
                    return ctx -> Arrays.stream(element.getAnnotations()).collect(Collectors.toCollection(LinkedHashSet::new));
                }

                @Override
                public QueryFunction<Store, Annotation> of(AnnotatedElement element) {
                    return ReflectionUtils.extendType().get(element).getAll(ANNOTATIONS::get, Annotation::annotationType);
                }
            };

    /**
     * query annotation types <pre>{@code get(AnnotationTypes.of(element)) -> Set<Class<? extends Annotation>> }</pre>
     */
    public static final UtilQueryBuilder<AnnotatedElement, Class<? extends Annotation>> ANNOTATION_TYPES =
            new UtilQueryBuilder<AnnotatedElement, Class<? extends Annotation>>() {
                @Override
                public QueryFunction<Store, Class<? extends Annotation>> get(AnnotatedElement element) {
                    return ANNOTATIONS.get(element).map(Annotation::annotationType);
                }

                @Override
                public QueryFunction<Store, Class<? extends Annotation>> of(AnnotatedElement element) {
                    return ReflectionUtils.extendType().get(element).getAll(ANNOTATION_TYPES::get, a -> a);
                }
            };

    /**
     * query methods <pre>{@code get(Methods.of(type)) -> Set<Method>}</pre>
     */
    public static final UtilQueryBuilder<Class<?>, Method> METHODS =
            element -> ctx -> Arrays.stream(element.getDeclaredMethods()).filter(NOT_OBJECT_METHOD).collect(Collectors.toCollection(LinkedHashSet::new));

    /**
     * query constructors <pre>{@code get(Constructors.of(type)) -> Set<Constructor> }</pre>
     */
    public static final UtilQueryBuilder<Class<?>, Constructor> CONSTRUCTORS =
            element -> ctx -> Arrays.<Constructor>stream(element.getDeclaredConstructors()).collect(Collectors.toCollection(LinkedHashSet::new));

    /**
     * query fields <pre>{@code get(Fields.of(type)) -> Set<Field> }</pre>
     */
    public static final UtilQueryBuilder<Class<?>, Field> FIELDS =
            element -> ctx -> Arrays.stream(element.getDeclaredFields()).collect(Collectors.toCollection(LinkedHashSet::new));

    /**
     * query url resources using {@link ClassLoader#getResources(String)} <pre>{@code get(Resources.with(name)) -> Set<URL> }</pre>
     */
    public static final UtilQueryBuilder<String, URL> RESOURCES =
            element -> ctx -> new HashSet<>(AbstractClasspathHelper.forResource(element));

    public static <T extends AnnotatedElement> UtilQueryBuilder<AnnotatedElement, T> extendType() {
        return element -> {
            if (element instanceof Class && !((Class<?>) element).isAnnotation()) {
                QueryFunction<Store, Class<?>> single = QueryFunction.single((Class<?>) element);
                return (QueryFunction<Store, T>) single.add(single.getAll(SUPER_TYPES::get));
            } else {
                return QueryFunction.single((T) element);
            }
        };
    }

    /**
     * get all annotations of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Annotations.of())}
     */
    public static <T extends AnnotatedElement> Set<Annotation> getAllAnnotations(T type, Predicate<Annotation>... predicates) {
        return get(ANNOTATIONS.of(type), predicates);
    }

    /**
     * get all super types of given {@code type}, including, optionally filtered by {@code predicates}
     */
    public static Set<Class<?>> getAllSuperTypes(final Class<?> type, Predicate<? super Class<?>>... predicates) {
        Predicate<? super Class<?>>[] filter = predicates == null || predicates.length == 0 ? new Predicate[]{t -> !Object.class.equals(t)} : predicates;
        return get(SUPER_TYPES.of(type), filter);
    }

    /**
     * get the immediate supertype and interfaces of the given {@code type}
     * <p>marked for removal, use instead {@code get(SuperTypes.get())}
     */
    public static Set<Class<?>> getSuperTypes(Class<?> type) {
        return get(SUPER_TYPES.get(type));
    }

    /**
     * get all methods of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Methods.of())}
     */
    public static Set<Method> getAllMethods(final Class<?> type, Predicate<? super Method>... predicates) {
        return get(METHODS.of(type), predicates);
    }

    /**
     * get methods of given {@code type}, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Methods.get())}
     */
    public static Set<Method> getMethods(Class<?> t, Predicate<? super Method>... predicates) {
        return get(METHODS.get(t), predicates);
    }

    /**
     * get all constructors of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Constructors.of())}
     */
    public static Set<Constructor> getAllConstructors(final Class<?> type, Predicate<? super Constructor>... predicates) {
        return get(CONSTRUCTORS.of(type), predicates);
    }

    /**
     * get constructors of given {@code type}, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Constructors.get())}
     */
    public static Set<Constructor> getConstructors(Class<?> t, Predicate<? super Constructor>... predicates) {
        return get(CONSTRUCTORS.get(t), predicates);
    }

    /**
     * get all fields of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Fields.of())}
     */
    public static Set<Field> getAllFields(final Class<?> type, Predicate<? super Field>... predicates) {
        return get(FIELDS.of(type), predicates);
    }

    /**
     * get fields of given {@code type}, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Fields.get())}
     */
    public static Set<Field> getFields(Class<?> type, Predicate<? super Field>... predicates) {
        return get(FIELDS.get(type), predicates);
    }

    /**
     * get annotations of given {@code type}, optionally honorInherited, optionally filtered by {@code predicates}
     * <p>marked for removal, use instead {@code get(Annotations.get())}
     */
    public static <T extends AnnotatedElement> Set<Annotation> getAnnotations(T type, Predicate<Annotation>... predicates) {
        return get(ANNOTATIONS.get(type), predicates);
    }

    /**
     * map {@code annotation} to hash map of member values recursively <pre>{@code Annotations.of(type).map(ReflectionUtils::toMap)} </pre>
     */
    public static Map<String, Object> toMap(Annotation annotation) {
        return get(METHODS.of(annotation.annotationType())
                .filter(NOT_OBJECT_METHOD.and(withParametersCount(0))))
                .stream()
                .collect(Collectors.toMap(Method::getName, m -> {
                    Object v1 = invoke(m, annotation);
                    return v1.getClass().isArray() && v1.getClass().getComponentType().isAnnotation() ?
                            Stream.of((Annotation[]) v1).map(ReflectionUtils::toMap).collect(toList()) : v1;
                }));
    }

    /**
     * map {@code annotation} and {@code annotatedElement} to hash map of member values
     * <pre>{@code Annotations.of(type).map(a -> toMap(type, a))} </pre>
     */
    public static Map<String, Object> toMap(Annotation annotation, AnnotatedElement element) {
        Map<String, Object> map = toMap(annotation);
        if (element != null) {
            map.put("annotatedElement", element);
        }
        return map;
    }

    /**
     * create new annotation proxy with member values from the given {@code map} <pre>{@code toAnnotation(Map.of("annotationType", annotationType, "value", ""))}</pre>
     */
    public static Annotation toAnnotation(Map<String, Object> map) {
        return toAnnotation(map, (Class<? extends Annotation>) map.get("annotationType"));
    }

    /**
     * create new annotation proxy with member values from the given {@code map} and member values from the given {@code map}
     * <pre>{@code toAnnotation(Map.of("value", ""), annotationType)}</pre>
     */
    public static <T extends Annotation> T toAnnotation(Map<String, Object> map, Class<T> annotationType) {
        return (T) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class<?>[]{annotationType},
                (proxy, method, args) -> NOT_OBJECT_METHOD.test(method) ? map.get(method.getName()) : method.invoke(map));
    }

    /**
     * invoke the given {@code method} with {@code args}, return either the result or an exception if occurred
     */
    public static Object invoke(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            return e;
        }
    }

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name} and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field (may be {@code null} if type is specified)
     * @param type  the type of the field (may be {@code null} if name is specified)
     * @return the corresponding Field object, or {@code null} if not found
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            List<Field> fields = ClassUtils.getFields(searchType);
            for (Field field : fields) {
                boolean b = (name == null || name.equals(field.getName())) &&
                        (type == null || type.equals(field.getType()));
                if (b) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Make the given constructor accessible, explicitly setting it accessible
     * if necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * @param ctor the constructor to make accessible
     * @see Constructor#setAccessible
     */
    @SuppressWarnings("deprecation")
    public static void makeAccessible(Constructor<?> ctor) {
        boolean b = (!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible();
        if (b) {
            ctor.setAccessible(true);
        }
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * @param field the field to make accessible
     * @see Field#setAccessible
     */
    @SuppressWarnings("deprecation")
    public static void makeAccessible(Field field) {
        boolean b = (!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible();
        if (b) {
            field.setAccessible(true);
        }
    }

    /**
     * Set the field represented by the supplied {@linkplain Field field object} on
     * the specified {@linkplain Object target object} to the specified {@code value}.
     * <p>In accordance with {@link Field#set(Object, Object)} semantics, the new value
     * is automatically unwrapped if the underlying field has a primitive type.
     * <p>This method does not support setting {@code static final} fields.
     *
     * @param field  the field to set
     * @param target the target object on which to set the field
     *               (or {@code null} for a static field)
     * @param value  the value to set (may be {@code null})
     */
    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取字段值
     *
     * @param entity    实体
     * @param fieldName 字段名称
     * @return 属性值
     */
    public static Object getFieldValue(Object entity, String fieldName) {
        Class cls = entity.getClass();
        Map<String, Field> fieldMaps = getFieldMap(cls);
        try {
            Field field = fieldMaps.get(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        List<Field> fieldList = ClassUtils.getFields(clazz);
        return !CollectionUtils.isEmpty(fieldList) ? fieldList.stream().collect(Collectors.toMap(Field::getName, field -> field)) : Collections.emptyMap();
    }

    /**
     * 判断是否为基本类型或基本包装类型
     *
     * @param clazz class
     * @return 是否基本类型或基本包装类型
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return (clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz));
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        return ClassUtils.getFields(clazz);
    }
}
