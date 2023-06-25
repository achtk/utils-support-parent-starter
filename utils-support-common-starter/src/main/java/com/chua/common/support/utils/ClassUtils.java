package com.chua.common.support.utils;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.MethodFilter;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.modularity.resolver.ModularityTypeResolver;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.value.*;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.converter.Converter.convertIfPrimitive;

/**
 * 类处理工具
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ClassUtils {


    private static final String[] RT_PACKAGE = new String[]{
            "java.*",
            "javax.*",
            "jdk.*",
            "com.oracle.*",
            "com.sun.*"
    };
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap<>(256);
    /**
     * 基础类型与封装关系
     */
    public static final Map<Class<?>, Class<?>> PRIMITIVE_PACK = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
        {
            put(byte.class, Byte.class);
            put(boolean.class, Boolean.class);
            put(short.class, Short.class);
            put(char.class, Character.class);
            put(int.class, Integer.class);
            put(float.class, Float.class);
            put(long.class, Long.class);
            put(double.class, Double.class);
            put(void.class, Void.class);
        }
    });
    /**
     * 空 map
     */
    public static final Table<Object, Class<?>, Class<?>> BASIC_VIRTUAL = new ConcurrentReferenceTable<>();

    static {
        BASIC_VIRTUAL.put("B", byte.class, Byte.class);
        BASIC_VIRTUAL.put("I", int.class, Integer.class);
        BASIC_VIRTUAL.put("D", double.class, Double.class);
        BASIC_VIRTUAL.put("F", float.class, Float.class);
        BASIC_VIRTUAL.put("C", char.class, Character.class);
        BASIC_VIRTUAL.put("Z", boolean.class, Boolean.class);
        BASIC_VIRTUAL.put("S", short.class, Short.class);
        BASIC_VIRTUAL.put("J", long.class, Long.class);
    }

    /**
     * 代理 class 的名称
     */
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
            // cglib
            , "org.springframework.cglib.proxy.Factory"
            , "javassist.util.proxy.ProxyObject"
            // javassist
            , "org.apache.ibatis.javassist.util.proxy.ProxyObject");

    protected static final Map<Class<?>, Type[]> ACTUAL = new ConcurrentReferenceHashMap<>(256);
    /**
     * 类-字段关系
     */
    protected static final Map<Class<?>, List<Field>> CLASS_FIELD = new ConcurrentReferenceHashMap<>(256);
    /**
     * 类-字段关系
     */
    protected static final Map<Class<?>, List<Field>> CLASS_FIELD_LOCAL = new ConcurrentReferenceHashMap<>(256);
    /**
     * 类-方法关系
     */
    protected static final Map<Class<?>, List<Method>> CLASS_METHOD = new ConcurrentReferenceHashMap<>(256);
    /**
     * 类-方法关系
     */
    protected static final Map<Class<?>, List<Method>> CLASS_METHOD_LOCAL = new ConcurrentReferenceHashMap<>(256);

    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(9);

    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_MAP = new IdentityHashMap<>(9);

    /**
     * Map with primitive type name as key and corresponding primitive
     * type as value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new ConcurrentReferenceHashMap<>(32);

    /**
     * Map with common Java language class name as key and corresponding Class as value.
     * Primarily for efficient deserialization of remote invocations.
     */
    private static final Map<String, Class<?>> COMMON_CLASS_CACHE = new ConcurrentReferenceHashMap<>(64);

    /**
     * Suffix for array class names: {@code "[]"}.
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * Prefix for internal array class names: {@code CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET}.
     */
    private static final String INTERNAL_ARRAY_PREFIX = SYMBOL_LEFT_SQUARE_BRACKET;

    /**
     * Prefix for internal non-primitive array class names: {@code "[L"}.
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /**
     * A reusable empty class array constant.
     */
    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    /**
     * The package separator character: {@code '.'}.
     */
    private static final char PACKAGE_SEPARATOR = '.';

    /**
     * The path separator character: {@code '/'}.
     */
    private static final char PATH_SEPARATOR = '/';

    /**
     * The nested class separator character: {@code '$'}.
     */
    private static final char NESTED_CLASS_SEPARATOR = '$';
    /**
     * Common Java language interfaces which are supposed to be ignored
     * when searching for 'primary' user-level interfaces.
     */
    private static final Set<Class<?>> JAVA_LANGUAGE_INTERFACES;

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Void.class, void.class);

        // Map entry iteration is less expensive to initialize than forEach with lambdas
        for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPER_TYPE_MAP.entrySet()) {
            PRIMITIVE_TYPE_TO_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }

        Set<Class<?>> primitiveTypes = new HashSet<>(32);
        primitiveTypes.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class,
                double[].class, float[].class, int[].class, long[].class, short[].class);
        for (Class<?> primitiveType : primitiveTypes) {
            PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
                Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
                Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
                Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class,
                Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);

        Class<?>[] javaLanguageInterfaceArray = {Serializable.class, Externalizable.class,
                Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
        registerCommonClasses(javaLanguageInterfaceArray);
        JAVA_LANGUAGE_INTERFACES = new HashSet<>(Arrays.asList(javaLanguageInterfaceArray));
    }

    /**
     * Register the given common classes with the ClassUtils cache.
     */
    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            COMMON_CLASS_CACHE.put(clazz.getName(), clazz);
        }
    }

    /**
     * 是否为空
     *
     * @param value 类
     * @param <T>   类型
     * @return 是否为空
     */
    public static <T> boolean isVoid(Class<T> value) {
        return null == value || value == void.class || value == Void.class;
    }

    /**
     * 是否为空
     *
     * @param value 类
     * @param <T>   类型
     * @return 是否为空
     */
    public static <T> boolean isVoid(T value) {
        return null == value || isVoid(toType(value));
    }

    /**
     * 获取默认类加载器
     *
     * @return 类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignored) {
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }

        if (cl == null) {
            try {
                cl = ClassLoader.getSystemClassLoader();
            } catch (Throwable ignored) {
            }
        }
        return cl;
    }


    /**
     * 判断类是否存在
     *
     * @param className 类名
     * @param consumer  回调
     */
    public static void ifPresent(String className, Consumer<Class<?>> consumer) {
        if (ClassUtils.isPresent(className)) {
            consumer.accept(ClassUtils.forName(className));
        }
    }

    /**
     * 判断类是否存在
     *
     * @param className 类名
     * @param consumer  回调
     */
    public static void ifPresent(String className) {
        if (ClassUtils.isPresent(className)) {
        }
    }

    /**
     * 是否包含类
     *
     * @param className 类名
     * @return 类加载器是否包含该类, 包含返回true
     */
    public static boolean isPresent(final String className) {
        return isPresent(className, getDefaultClassLoader());
    }

    private static final Map<String, Boolean> CACHE = new ConcurrentReferenceHashMap<>(512);
    /**
     * 是否包含类
     *
     * @param className   类名
     * @param classLoader 类加载器
     * @return 类加载器是否包含该类, 包含返回true
     */
    public static boolean isPresent(final String className, final ClassLoader classLoader) {
        return MapUtils.computeIfAbsent(CACHE, className, it -> {
            try {
                final Class<?> aClass = forName(className, classLoader);
                return null != aClass;
            } catch (Throwable ex) {
                return false;
            }
        });
    }

    /**
     * 字符转类
     *
     * @param name 名称
     * @return 类
     * @throws ClassNotFoundException 类不存在
     * @throws LinkageError           连接异常
     */
    public static Class<?> forName(String name) {
        if (null == name) {
            return null;
        }

        return forName(name, getDefaultClassLoader());
    }

    /**
     * <p>
     * 请仅在确定类存在的情况下调用该方法
     * </p>
     *
     * @param name        类名称
     * @param classLoader 联系加载器
     * @return 返回转换后的 Class
     */
    public static Class<?> toClassConfident(String name, ClassLoader classLoader) {
        try {
            return Class.forName(name, true, classLoader);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ex) {
                try {
                    throw new ClassNotFoundException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
                } catch (ClassNotFoundException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
    }

    /**
     * <p>
     * 请仅在确定类存在的情况下调用该方法
     * </p>
     *
     * @param name 类名称
     * @return 返回转换后的 Class
     */
    public static Class<?> toClassConfident(String name) {
        return toClassConfident(name, getDefaultClassLoader());
    }

    /**
     * 字符转类
     *
     * @param name       名称
     * @param returnType 返回类型
     * @return 类
     * @throws LinkageError ex 连接异常
     */
    public static <T> Class<T> forName(String name, Class<T> returnType) {
        Class<?> aClass = null;
        try {
            aClass = forName(name);
        } catch (Exception e) {
            return null;
        }
        return null == aClass || returnType.isAssignableFrom(aClass) ? (Class<T>) aClass : null;
    }

    /**
     * 字符转类
     *
     * @param name        名称
     * @param classLoader 类加载器
     * @return 类
     * @throws LinkageError 连接异常
     */
    public static Class<?> forName(String name, ClassLoader classLoader) {

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = COMMON_CLASS_CACHE.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(SYMBOL_SEMICOLON)) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }
        try {
            return Class.forName(name, false, clToUse);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDotIndex != -1) {
                String nestedClassName =
                        name.substring(0, lastDotIndex) + NESTED_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
                try {
                    return Class.forName(nestedClassName, false, clToUse);
                } catch (ClassNotFoundException ex2) {
                }
            }
            return null;
        }
    }

    /**
     * 翻译封装类
     *
     * @param name 名称
     * @return 封装类
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 7) {
            result = PRIMITIVE_TYPE_NAME_MAP.get(name);
        }
        return result;
    }

    /**
     * 实例化类
     *
     * @param name   类
     * @param params 参数
     * @return 对象
     * @throws Exception ex
     */
    public static <T> T forObject(String name, Object... params) {
        try {
            return (T) forObject(forName(name), getDefaultClassLoader(), params);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 实例化类
     *
     * @param tClass 类
     * @param params 参数
     * @return 对象
     * @throws Exception ex
     */
    public static <T> T forObjectWithType(String typeName, Class<T> type, Object... params) {
        try {
            T forObject = (T) forObject(forName(typeName, type.getClassLoader()), params);
            if(null == forObject || !type.isAssignableFrom(forObject.getClass())) {
                return null;
            }
            return forObject;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 实例化类
     *
     * @param tClass 类
     * @param params 参数
     * @return 对象
     * @throws Exception ex
     */
    public static <T> T forObject(Class<T> tClass, Object... params) {
        try {
            return forObject(tClass, getDefaultClassLoader(), params);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 实例化类
     * <p>
     * <p>类加载过程:</p>
     * <p>1、首次尝试对类实例化<code>newInstance</code></p>
     * <p>2、当失败时, 检测类是否包含无参构造，不存在则创建无参构造，存在但是无参构造不为公共修饰符则构造初始化</p>
     * <p>3、当创建了无参构造再次尝试实例化，再次失败返回null</p>
     *
     * <p>接口/抽象类加载过程：</p>
     * <p>1、判断类为接口， 采用代理生成空的实现类，并返回</p>
     * </p>
     *
     * @param tClass      类
     * @param classLoader 类加载器
     * @param params      参数
     * @return 对象
     * @throws Exception ex
     */
    @SuppressWarnings("ALL")
    public static <T> T forObject(Class<T> tClass, ClassLoader classLoader, Object... params) throws Exception {
        if (null == tClass) {
            return null;
        }

        if (List.class.isAssignableFrom(tClass) || Collection.class.isAssignableFrom(tClass)) {
            return (T) new ArrayList();
        }

        if (Set.class.isAssignableFrom(tClass)) {
            return (T) new HashSet<>();
        }

        if (tClass.isInterface()) {
            return null;
        }

        if (null == params || params.length == 0) {
            T newInstance = tClass.newInstance();
            if (null != newInstance) {
                return newInstance;
            }
        }

        Class<?>[] classes = ClassUtils.toType(params);
        Constructor<T> declaredConstructor = ClassUtils.getConstructor(tClass, classes);
        if (null != declaredConstructor) {
            declaredConstructor.setAccessible(true);
            params = createArgs(params, declaredConstructor);
            try {
                return declaredConstructor.newInstance(params);
            } catch (Exception ignore) {
            }
        }
        return (T) createAlgorithm(tClass, params);
    }

    /**
     * 参数
     *
     * @param params              参数
     * @param declaredConstructor 构造
     * @param <T>                 类型
     * @return 结果
     */
    private static <T> Object[] createArgs(Object[] params, Constructor<T> declaredConstructor) {
        return createArgs(params, declaredConstructor.getParameterTypes());
    }

    private static Object[] createArgs(IndexValue indexValue, int[] index) {
        if (index.length == 0) {
            return indexValue.getAll().stream().map(KeyValue::getValue).toArray(Object[]::new);
        }

        return Arrays.stream(index).mapToObj(it -> {
            return indexValue.get(it).getValue();
        }).toArray(Object[]::new);
    }

    /**
     * 参数
     *
     * @param params              参数
     * @param declaredConstructor 构造
     * @param <T>                 类型
     * @return 结果
     */
    public static Object[] createArgs(Object[] params, Class<?>[] parameterTypes) {
        Object[] rs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object param = params[i];
            rs[i] = param;
            if (null == param) {
                continue;
            }

            if (parameterType.isAssignableFrom(param.getClass())) {
                continue;
            }

            if(parameterType.getTypeName().equals(param.getClass().getTypeName())) {
                continue;
            }

            if (parameterType.isInterface()) {
                rs[i] = ProxyUtils.newProxy(parameterType, new BridgingMethodIntercept(parameterType, param));
                continue;
            }

            rs[i] = Converter.convertIfNecessary(param, parameterType);
        }
        return rs;
    }

    /**
     * 创建实体
     *
     * @param params 参数
     * @return 实体
     */
    @SuppressWarnings("all")
    private static <T> T createAlgorithm(Class<T> tClass, Object[] params) {
        Map<Constructor<?>, Object[]> loss = new LinkedHashMap<>();
        Map<Constructor<?>, Object[]> allnull = new LinkedHashMap<>();

        Map<Class<?>, Object> typeAndValue = createTypeAndValue(params);
        Constructor<?>[] declaredConstructors = tClass.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            T algorithm = null;
            try {
                algorithm = createAlgorithm(declaredConstructor, typeAndValue, loss, allnull);
            } catch (Exception ignored) {
            }
            if (null != algorithm) {
                return algorithm;
            }
        }

        if (!loss.isEmpty()) {
            for (Map.Entry<Constructor<?>, Object[]> entry : loss.entrySet()) {
                Constructor<?> constructor = entry.getKey();
                constructor.setAccessible(true);
                try {
                    Object newInstance = constructor.newInstance(entry.getValue());
                    if (null != newInstance) {
                        return (T) newInstance;
                    }
                } catch (Exception ignored) {
                }
            }
        }


        if (!allnull.isEmpty()) {
            for (Map.Entry<Constructor<?>, Object[]> entry : allnull.entrySet()) {
                Constructor<?> constructor = entry.getKey();
                constructor.setAccessible(true);
                try {
                    Object newInstance = constructor.newInstance(entry.getValue());
                    if (null != newInstance) {
                        return (T) newInstance;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 创建实体
     *
     * @param declaredConstructor 构造
     * @param params              参数
     * @param loss                用于存储参数有空的对象，先处理全非空后处理存在空
     * @param allNUll             用于存储参数全控，先处理全非空后处理存在空
     * @return 实体
     */
    @SuppressWarnings("all")
    private static <T> T createAlgorithm(Constructor<?> declaredConstructor, Map<Class<?>, Object> params, Map<Constructor<?>, Object[]> loss,
                                         Map<Constructor<?>, Object[]> allNUll) throws Exception {
        Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
        if (parameterTypes.length == 0) {
            declaredConstructor.setAccessible(true);
            return (T) declaredConstructor.newInstance();
        }

        Object[] args = getArgs(parameterTypes, params);

        if (isAllNull(args)) {
            allNUll.put(declaredConstructor, args);
            return null;
        }

        if (hasNone(args)) {
            declaredConstructor.setAccessible(true);
            return (T) declaredConstructor.newInstance(args);
        }

        loss.put(declaredConstructor, args);
        return null;
    }

    /**
     * 获取集合类型
     *
     * @param collection 集合
     * @return class
     * @throws NullPointerException ex
     */
    public static Class<?>[] toType(Object[] collection) {
        if (null == collection || collection.length == 0) {
            return new Class[0];
        }

        Class<?>[] rs = new Class<?>[collection.length];

        int index = 0;
        for (Object o : collection) {
            rs[index++] = toType(o);
        }
        return rs;
    }

    /**
     * 获取对象
     *
     * @param object 对象/类
     * @return 类
     */
    public static Class<?> toType(Object object) {
        if (object == null) {
            return Void.class;
        }

        if (object instanceof Class<?>) {
            return (Class<?>) object;
        }

        if (object instanceof String && ((String) object).contains(SYMBOL_DOT)) {
            Class<?> aClass = ClassUtils.forName(object.toString());
            if (null != aClass && void.class != aClass) {
                return ObjectUtils.defaultIfNull(aClass, void.class);
            }
        }

        Class<?> aClass = object.getClass();
        if (!Proxy.isProxyClass(aClass)) {
            return ObjectUtils.defaultIfNull(aClass, void.class);
        }

        String toString = object.toString();
        if (!toString.contains("$Proxy")) {
            Class<?> aClass1 = forName(StringUtils.removeSuffixContains(toString.replace("@", ""), "("), object.getClass().getClassLoader());
            return null == aClass1 ? void.class : ObjectUtils.defaultIfNull(aClass1, void.class);
        }
        return void.class;
    }

    /**
     * 获取对象
     *
     * @param object 对象/类
     * @return 对象
     */
    public static Object asObject(Object object) {
        if (object instanceof Class) {
            Class<?> type = fromPrimitive((Class<?>) object);
            return ClassUtils.forObject(type);
        }

        if (object instanceof String) {
            return ClassUtils.forObject(object.toString());
        }
        return object;
    }

    /**
     * 基础类转封装类
     * <p> There are nine predefined {@code Class} objects to represent
     * the eight primitive types and void.  These are created by the Java
     * Virtual Machine, and have the same names as the primitive types that
     * they represent, namely {@code boolean}, {@code byte},
     * {@code char}, {@code short}, {@code int},
     * {@code long}, {@code float}, and {@code double}.
     *
     * <p> These objects may only be accessed via the following public static
     * final variables, and are the only {@code Class} objects for which
     * this method returns {@code true}.
     *
     * @param target 类
     * @param <T>    类型
     * @return 封装类
     * @see java.lang.Boolean#TYPE
     * @see java.lang.Character#TYPE
     * @see java.lang.Byte#TYPE
     * @see java.lang.Short#TYPE
     * @see java.lang.Integer#TYPE
     * @see java.lang.Long#TYPE
     * @see java.lang.Float#TYPE
     * @see java.lang.Double#TYPE
     * @see java.lang.Void#TYPE
     */
    public static <T> Class<T> fromPrimitive(Class<T> target) {
        if (target.isPrimitive()) {
            if (PRIMITIVE_PACK.containsKey(target)) {
                return (Class<T>) PRIMITIVE_PACK.get(target);
            }
        }

        String name = target.getName();
        if (name.startsWith(SYMBOL_LEFT_SQUARE_BRACKET)) {
            String all = name.replaceAll("\\[", "");
            if (BASIC_VIRTUAL.containsRow(all)) {
                Map<Class<?>, Class<?>> immutableMap = BASIC_VIRTUAL.row(all);
                return (Class<T>) ClassUtils.forName(name.replace(all, immutableMap.values().iterator().next().getName()));
            }
        }
        return target;
    }

    /**
     * 获取名称
     *
     * @param annotations 注解
     * @return 名称
     */
    public static String[] toTypeName(Annotation[] annotations) {
        if (null == annotations || annotations.length == 0) {
            return EMPTY_STRING_ARRAY;
        }
        String[] result = new String[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            Annotation parameterType = annotations[i];
            result[i] = parameterType.annotationType().getTypeName();
        }

        return result;
    }

    /**
     * 获取名称
     *
     * @param parameterTypes 类型
     * @return 名称
     */
    public static String[] toTypeName(Class<?>[] parameterTypes) {
        if (null == parameterTypes || parameterTypes.length == 0) {
            return EMPTY_STRING_ARRAY;
        }
        String[] result = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            result[i] = parameterType.getTypeName();
        }

        return result;
    }

    /**
     * 获取泛型类型
     * <p>1.先判断父类泛型</p>
     * <p>2.判断接口泛型</p>
     *
     * @param value 类
     * @return 泛型类型
     */
    public static Type[] getActualTypeArguments(final Class<?> value, final Class<?>... includes) {
        return ACTUAL.computeIfAbsent(value, it -> {
            Type type = value.getGenericSuperclass();
            List<Type> types = new ArrayList<>();
            if (type instanceof ParameterizedType && container(type, includes)) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                types.addAll(Arrays.asList(actualTypeArguments));
            }

            Type[] genericInterfaces = value.getGenericInterfaces();
            for (Type anInterface : genericInterfaces) {
                if (anInterface instanceof ParameterizedType && container(anInterface, includes)) {
                    ParameterizedType parameterizedType = (ParameterizedType) anInterface;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    for (Type actualTypeArgument : actualTypeArguments) {
                        if (actualTypeArgument instanceof ParameterizedType) {
                            types.add(((ParameterizedType) actualTypeArgument).getRawType());
                        } else {
                            types.add(actualTypeArgument);
                        }
                    }
                }
            }
            return types.toArray(new Type[0]);
        });

    }

    /**
     * 是否包含类
     *
     * @param type     类
     * @param includes 类集合
     * @return 包含返回true
     */
    private static boolean container(Type type, Class<?>[] includes) {
        if (null == type || null == includes || includes.length == 0) {
            return true;
        }

        String typeName = type.getTypeName();
        for (Class<?> include : includes) {
            int index = typeName.indexOf("<");
            if (index != -1) {
                typeName = typeName.substring(0, index);
            }
            if (typeName.equals(include.getName())) {
                return true;
            }
        }

        return false;
    }


    /**
     * 获得ClassPath，将编码后的中文路径解码为原字符<br>
     * 这个ClassPath路径会文件路径被标准化处理
     *
     * @return ClassPath
     */
    public static String getClassPath() {
        return getClassPath(false);
    }

    /**
     * 获得ClassPath，这个ClassPath路径会文件路径被标准化处理
     *
     * @param isEncoded 是否编码路径中的中文
     * @return ClassPath
     * @since 3.2.1
     */
    public static String getClassPath(boolean isEncoded) {
        final java.net.URL url1 = getClassPathUrl();
        String url = isEncoded ? url1.getPath() : UrlUtils.getDecodedPath(url1);
        return FileUtils.normalize(url);
    }

    /**
     * 获得ClassPath URL
     *
     * @return ClassPath URL
     */
    public static java.net.URL getClassPathUrl() {
        return getResourceUrl(SYMBOL_EMPTY);
    }

    /**
     * 获得资源的URL<br>
     * 路径用/分隔，例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param source 资源（相对Classpath的路径）
     * @return 资源URL
     */
    public static URL getResourceUrl(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        File temp = new File(source);
        try {
            if (temp.exists()) {
                return temp.toURI().toURL();
            }
        } catch (MalformedURLException ignored) {
        }
        return ClassUtils.class.getClassLoader().getResource(source);
    }

    /**
     * java类
     *
     * @param type 类型
     * @return java类
     */
    public static boolean isJavaType(Class<?> type) {
        return type.getTypeName().startsWith("java.") ||
                type.isPrimitive()
                ;
    }


    /**
     * 字段查询
     *
     * @param aClass   处理类
     * @param callback 回调
     */
    public static void doWithFields(final Class<?> aClass, final Consumer<Field> callback) {
        try {
            for (Field field : getFields(aClass)) {
                try {
                    callback.accept(field);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
        } catch (Throwable ignore) {
        }
    }

    /**
     * 获取所有字段
     *
     * @param obj       对象
     * @param fieldName 字段名称
     * @return 所有字段
     */
    public static Field getFields(final Object obj, String fieldName) {
        if (null == obj) {
            return null;
        }

        for (Field field : getFields(obj)) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /**
     * 获取所有字段
     *
     * @param obj 对象
     * @return 所有字段
     */
    public static List<Field> getFields(final Object obj) {
        if (null == obj) {
            return Collections.emptyList();
        }
        final Class<?> aClass = ClassUtils.toType(obj);
        return MapUtils.getComputeIfFunction(CLASS_FIELD_LOCAL, aClass, aClass12 -> {
            List<Field> result = new ArrayList<>();
            Class<?> newClass = aClass12;
            while (!ClassUtils.isObject(newClass)) {
                Field[] fields = new Field[0];
                try {
                    fields = newClass.getDeclaredFields();
                    result.addAll(Arrays.asList(fields));
                    newClass = newClass.getSuperclass();
                } catch (Throwable ignored) {
                    break;
                }
            }
            return result;
        });
    }


    /**
     * 字段查询
     *
     * @param aClass   处理类
     * @param callback 回调
     */
    public static void doWithLocalFields(final Class<?> aClass, final Consumer<Field> callback) {
        for (Field field : getLocalFields(aClass)) {
            try {
                callback.accept(field);
            } catch (Throwable ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }


    /**
     * 方法回调
     *
     * @param aClass   类
     * @param callback 方法回调
     */
    public static void doWithLocalMethods(final Class<?> aClass, final Consumer<Method> callback) {
        if (null == aClass || null == callback) {
            return;
        }
        for (Method method : getLocalMethods(aClass)) {
            try {
                callback.accept(method);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + throwable);
            }
        }
    }

    /**
     * 方法回调
     *
     * @param aClass       类
     * @param callback     方法回调
     * @param methodFilter 过滤器
     */
    public static void doWithMethods(final Class<?> aClass, final Consumer<Method> callback, MethodFilter methodFilter) {
        if (null == aClass || null == callback) {
            return;
        }
        for (Method method : getMethods(aClass)) {
            if (methodFilter != null && !methodFilter.matches(method)) {
                continue;
            }
            try {
                callback.accept(method);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + throwable);
            }
        }
    }

    /**
     * 方法回调
     *
     * @param aClass   类
     * @param callback 方法回调
     */
    public static void doWithMethods(final Class<?> aClass, final Consumer<Method> callback) {
        doWithMethods(aClass, callback, null);
    }

    /**
     * 获取当前类所有字段
     *
     * @param obj 对象
     * @return 所有字段
     */
    public static List<Field> getLocalFields(final Object obj) {
        if (null == obj) {
            return Collections.emptyList();
        }
        final Class<?> aClass = ClassUtils.toType(obj);

        return MapUtils.getComputeIfFunction(CLASS_FIELD, aClass, it -> {
            Field[] fields = aClass.getDeclaredFields();
            return Arrays.asList(fields);
        });
    }

    /**
     * 获取所有方法
     *
     * @param obj 对象
     * @return 所有字段
     */
    public static List<Method> getMethods(final Object obj) {
        if (null == obj) {
            return Collections.emptyList();
        }
        final Class<?> aClass = ClassUtils.toType(obj);

        return MapUtils.getComputeIfFunction(CLASS_METHOD, aClass, it -> {
            List<Method> result = new ArrayList<>();
            Class<?> newClass = aClass;
            while (!ClassUtils.isObject(newClass)) {
                Method[] methods = newClass.getDeclaredMethods();
                result.addAll(Arrays.asList(methods));
                newClass = newClass.getSuperclass();
            }

            Class<?>[] interfaces = aClass.getInterfaces();
            Set<Class<?>> allInterfaces = new HashSet<>();
            for (Class<?> anInterface : interfaces) {
                Class<?>[] newInterface1 = anInterface.getInterfaces();
                allInterfaces.add(anInterface);
                allInterfaces.addAll(Arrays.asList(newInterface1));
            }

            for (Class<?> allInterface : allInterfaces) {
                Method[] methods = allInterface.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isDefault()) {
                        result.add(method);
                    }
                }
            }

            return result;
        });
    }

    /**
     * 获取所有方法
     *
     * @param obj 对象
     * @return 所有字段
     */
    public static List<Method> getLocalMethods(final Object obj) {
        if (null == obj) {
            return Collections.emptyList();
        }
        final Class<?> aClass = ClassUtils.toType(obj);

        return MapUtils.getComputeIfFunction(CLASS_METHOD_LOCAL, aClass, it -> {
            Method[] methods = aClass.getDeclaredMethods();
            return Arrays.asList(methods);
        });
    }

    /**
     * 构造
     *
     * @param tClass  类型
     * @param classes 参数
     * @param <T>     类型
     * @return 构造
     */
    public static <T> Constructor<T> getConstructor(Class<T> tClass, Class<?>[] classes) {
        Constructor<?>[] declaredConstructors = tClass.getDeclaredConstructors();
        Constructor<?> item = null;
        boolean isTrue = true;
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Class<?> aClass = classes[i];
                if (aClass.getTypeName() == parameterType.getTypeName()) {
                    continue;
                }

                boolean b = !parameterType.isAssignableFrom(aClass) && !(Void.class.isAssignableFrom(aClass) || void.class.isAssignableFrom(aClass));
                if (b) {
                    isTrue = false;
                    break;
                }
            }
            if (isTrue) {
                item = declaredConstructor;
                break;
            }
        }

        return (Constructor<T>) item;
    }


    /**
     * 类是否是Object.class
     *
     * @param clazz 类
     * @return 类是 Object.class 或者null 返回true
     */
    @SuppressWarnings("all")
    public static boolean isObject(Class<?> clazz) {
        return null == clazz || Object.class.getName().equals(clazz.getName());
    }


    /**
     * 是否有空
     *
     * @param args 参数
     * @return 是否有空
     */
    private static boolean hasNone(Object[] args) {
        for (Object arg : args) {
            if (null == arg) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否全空
     *
     * @param args 参数
     * @return 是否全空
     */
    private static boolean isAllNull(Object[] args) {
        for (Object arg : args) {
            if (null != arg) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取对象
     *
     * @param parameterTypes 类型
     * @param params         参数
     * @return 参数
     */
    private static Object[] getArgs(Class<?>[] parameterTypes, Map<Class<?>, Object> params) {
        Object[] rs = new Object[parameterTypes.length];
        int index = 0;
        for (Class<?> aClass : parameterTypes) {
            Object o = params.get(aClass);
            rs[index++] = createValue(o, aClass, params);
        }

        return rs;
    }

    /**
     * 获取值
     *
     * @param o      获取到的对象
     * @param aClass 值的类型
     * @param params 参数集合
     * @return 值
     */
    private static Object createValue(Object o, Class<?> aClass, Map<Class<?>, Object> params) {
        if (null != o) {
            return o;
        }

        for (Map.Entry<Class<?>, Object> entry : params.entrySet()) {
            if (void.class == entry.getKey()) {
                continue;
            }

            if (aClass.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (params.size() == 1) {
            Map.Entry<Class<?>, Object> first = MapUtils.getFirst(params);
            Object necessary = Converter.convertIfNecessary(first.getValue(), aClass);
            if (null != necessary) {
                return necessary;
            }
        }

        return null;
    }

    /**
     * 类型与值
     *
     * @param params 值
     * @return 类型与值
     */
    private static Map<Class<?>, Object> createTypeAndValue(Object[] params) {
        Map<Class<?>, Object> rs = new HashMap<>(params.length);
        for (Object param : params) {
            if (null == param) {
                rs.put(void.class, null);
            } else {
                rs.put(param.getClass(), param);
            }
        }

        return rs;
    }

    /**
     * 执行方法
     *
     * @param object     对象
     * @param type       类型
     * @param methodName 方法
     * @param args       参数
     * @return 结果
     */
    public static Object invokeMethodChain(Object object, Class<?> type, String methodName, Object... args) {
        if (null == type) {
            return null;
        }

        Method method = findMethod(type, methodName, toType(args));
        if (null == method) {
            return null;
        }
        method.setAccessible(true);
        return invokeMethod(method, object, args);
    }

    /**
     * 执行方法
     *
     * @param bean       类型
     * @param methodName 方法
     * @param args       参数
     * @return 结果
     */
    public static Object invokeBean(Object bean, String methodName, Object... args) {
        if (null == bean) {
            return null;
        }

        Class<?> type = toType(bean);

        Method method = findMethod(type, methodName, toType(args));
        if (null == method) {
            return null;
        }
        setAccessible(method);
        return invokeMethod(method, bean, args);
    }

    /**
     * 执行方法
     *
     * @param type       类型
     * @param methodName 方法
     * @param args       参数
     * @return 结果
     */
    public static Object invokeMethod(Class<?> type, String methodName, Object... args) {
        if (null == type) {
            return null;
        }

        Method method = findMethod(type, methodName, toType(args));
        if (null == method) {
            return null;
        }

        return invokeMethod(method, null, args);
    }

    /**
     * 执行方法
     *
     * @param method 方法
     * @param bean   对象
     * @param args   参数
     * @return 结果
     */
    public static Object invokeMethod(Method method, Object bean, Object... args) {
        if (null == method) {
            return null;
        }

        if (bean instanceof Callable) {
            try {
                return ((Callable<?>) bean).call();
            } catch (Exception ignored) {
            }

            return null;
        }

        if (method.isDefault()) {
            return invokeDefaultMethod(method, bean, args);
        }
        try {
            return method.invoke(bean, method.getParameterCount() == 0 ? EMPTY_OBJECT : args);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 执行方法
     *
     * @param method 方法
     * @param bean   对象
     * @param args   参数
     * @return 结果
     */
    public static Object invokeDefaultMethod(Method method, Object bean, Object... args) {
        if (bean instanceof Callable) {
            try {
                return ((Callable<?>) bean).call();
            } catch (Exception ignored) {
            }
        }

        if (!method.isDefault()) {
            return null;
        }

        try {
            Constructor<?> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
            Class<?> declaringClass = method.getDeclaringClass();
            int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
            return ((MethodHandles.Lookup) constructor.newInstance(declaringClass, allModes))
                    .unreflectSpecial(method, declaringClass)
                    .bindTo(bean)
                    .invokeWithArguments(method.getParameterCount() == 0 ? EMPTY_OBJECT : args);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz      the class to introspect
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() :
                    getMethods(searchType).toArray(EMPTY_METHOD_ARRAY));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }


    /**
     * 查询字段
     *
     * @param type 类型
     * @param name 名称
     * @return 字段
     */
    public static Field findField(Class<?> type, String name) {
        List<Field> fields = getFields(type);
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        if (paramTypes.length != method.getParameterCount()) {
            return false;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (!parameterType.isAssignableFrom(paramTypes[i])) {
                return false;
            }
        }

        return true;
    }


    /**
     * 设置可访问对象的可访问权限为 true
     *
     * @param object 可访问的对象
     * @param <T>    类型
     * @return 返回设置后的对象
     */
    public static <T extends AccessibleObject> T setAccessible(T object) {
        return AccessController.doPrivileged(new SetAccessibleAction<>(object));
    }


    /**
     * 获取字段值
     *
     * @param fieldName 字段
     * @param target    类型
     * @param value     值
     */
    public static Object getFieldValue(String fieldName, Class<?> target, Object value) {
        List<Field> fields = getFields(target);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return getFieldValue(field, target, value);
            }
        }

        return null;
    }

    /**
     * 获取字段值
     *
     * @param field  字段
     * @param target 类型
     * @param value  值
     */
    public static Object getFieldValue(Field field, Class<?> target, Object value) {
        if (null == field) {
            return null;
        }

        String name = "get" + NamingCase.toFirstUpperCase(field.getName());
        try {
            Method declaredMethod = target.getDeclaredMethod(name);
            if (null != declaredMethod) {
                declaredMethod.setAccessible(true);
                return declaredMethod.invoke(value);
            }
        } catch (Exception ignore) {
        }

        try {
            Method declaredMethod = target.getDeclaredMethod(field.getName());
            if (null != declaredMethod) {
                declaredMethod.setAccessible(true);
                return declaredMethod.invoke(value);
            }
        } catch (Exception ignore) {
        }

        try {
            field.setAccessible(true);
            return field.get(value);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取字段值
     *
     * @param fieldName 字段
     * @param value     值
     */
    public static Object getFieldValue(String fieldName, Object value) {
        if (null == fieldName || null == value) {
            return null;
        }

        Field field = findField(value.getClass(), fieldName);
        setAccessible(field);
        return null == field ? null : getFieldValue(field, value);
    }

    /**
     * 获取字段值
     *
     * @param field 字段
     * @param value 值
     */
    public static Object getFieldValue(Field field, Object value) {
        if (null == field) {
            return null;
        }

        try {
            return field.get(value);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取字段值
     *
     * @param fieldName 名称
     * @param type      类型
     * @param value     值
     * @param bean      对象
     */
    public static void setFieldValue(String fieldName, Object value, Object bean) {
        if (ObjectUtils.isAnyEmpty(fieldName, value, bean)) {
            return;
        }

        setFieldValue(fieldName, bean.getClass(), value, bean);
    }

    /**
     * 获取字段值
     *
     * @param fieldName 名称
     * @param type      类型
     * @param value     值
     * @param bean      对象
     */
    public static void setFieldValue(String fieldName, Class<?> type, Object value, Object bean) {
        if (ObjectUtils.isAnyEmpty(fieldName, type, value)) {
            return;
        }

        List<Field> fields = getFields(type);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                setFieldValue(field, value, bean);
                break;
            }
        }
    }

    /**
     * 获取字段值
     *
     * @param field 字段
     * @param value 值
     * @param bean  对象
     */
    public static void setFieldValue(Field field, Object value, Object bean) {
        if (null == field) {
            return;
        }

        if (null == bean) {
            return;
        }

        setFieldValue(field, bean.getClass(), value, bean);
    }

    /**
     * 赋值
     *
     * @param field 字段
     * @param value 值
     * @param type  类型
     * @param bean  对象
     * @param <T>   类型
     */
    public static <T> void setAllFieldValue(Field field, Object value, Class<T> type, T bean) {
        Class<?> type1 = field.getType();
        value = Converter.convertIfNecessary(value, type1);
        String name = field.getName();
        try {
            Method method = type.getMethod(METHOD_SETTER + NamingCase.toFirstUpperCase(name), type1);
            if (null != method) {
                method.setAccessible(true);
                method.invoke(bean, value);
                return;
            }
        } catch (Exception ignore) {
        }

        try {
            field.setAccessible(true);
            field.set(bean, value);
        } catch (IllegalAccessException ignore) {
        }

    }

    /**
     * 获取字段值
     *
     * @param field  字段
     * @param target 类型
     * @param value  值
     * @param bean   对象
     */
    public static void setFieldValue(Field field, Class<?> target, Object value, Object bean) {
        if (null == field) {
            return;
        }


        String name = "set" + NamingCase.toFirstUpperCase(field.getName());
        try {
            Method declaredMethod = findMethod(target, name,  field.getType());
            if (null != declaredMethod) {
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(bean, value);
                return;
            }
        } catch (Exception ignore) {
        }

        try {
            Method declaredMethod = target.getDeclaredMethod(field.getName(), field.getType());
            if (null != declaredMethod) {
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(bean, value);
                return;
            }
        } catch (Exception ignore) {
        }

        try {
            setAccessible(field);
            field.set(bean, value);
        } catch (IllegalAccessException e) {
        }
    }


    /**
     * 类型是否相等
     *
     * @param targetType 目标类型
     * @param source     值
     * @return
     */
    public static boolean isEquals(Class<?> targetType, Object source) {
        if (null == source) {
            return true;
        }

        Class<?> aClass = source.getClass();
        if (targetType.isAssignableFrom(aClass)) {
            return true;
        }

        Object convertIfNecessary = Converter.convertIfNecessary(source, targetType);
        return null != convertIfNecessary;
    }

    /**
     * 执行方法
     *
     * @param object 对象
     * @param field  字段
     * @return 结果
     */
    public static Object invoke(InvocationHandler object, Field field) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Resolve the given class name into a Class instance. Supports
     * primitives (like "int") and array class names (like "String[]").
     * <p>This is effectively equivalent to the {@code forName}
     * method with the same arguments, with the only difference being
     * the exceptions thrown in case of class loading failure.
     *
     * @param className   the name of the Class
     * @param classLoader the class loader to use
     *                    (may be {@code null}, which indicates the default class loader)
     * @return a class instance for the supplied name
     * @throws IllegalArgumentException if the class name was not resolvable
     *                                  (that is, the class could not be found or the class file could not be loaded)
     * @throws IllegalStateException    if the corresponding class is resolvable but
     *                                  there was a readability mismatch in the inheritance hierarchy of the class
     *                                  (typically a missing dependency declaration in a Jigsaw module definition
     *                                  for a superclass or interface implemented by the class to be loaded here)
     * @see #forName(String, ClassLoader)
     */
    public static Class<?> resolveClassName(String className, ClassLoader classLoader)
            throws IllegalArgumentException {

        try {
            return forName(className, classLoader);
        } catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" +
                    className + "]: " + err.getMessage(), err);
        } catch (LinkageError err) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
        }
    }


    /**
     * 类型是否一致
     *
     * @param value  对象
     * @param target 类型
     * @return 类型是否一致
     */
    public static boolean isAssignableFrom(Object value, Class<?> target) {
        if (null == value && isVoid(target)) {
            return true;
        }

        if (null == value) {
            return false;
        }

        return target.isAssignableFrom(value.getClass());
    }

    /**
     * 返回一致类型的对象
     *
     * @param value  对象
     * @param target 类型
     * @return 返回一致类型的对象
     */
    public static <T> T withAssignableFrom(Object value, Class<T> target) {
        if (null == value && isVoid(target)) {
            return null;
        }

        return target.isAssignableFrom(value.getClass()) ? (T) value : null;
    }


    /**
     * 获取所有父类
     *
     * @param type 类
     * @return 所有接口
     */
    public static Set<Class<?>> getSuperType(Class<?> type) {
        Set<Class<?>> result = new HashSet<>();
        getSuperType(type, result);
        return result;
    }

    /**
     * 获取所有父类
     *
     * @param type   类
     * @param result 结果
     */
    private static void getSuperType(Class<?> type, Set<Class<?>> result) {
        if (null == type) {
            return;
        }

        Class<?> superclass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            result.add(anInterface);
        }
        if (Object.class == superclass || null == superclass) {
            return;
        }

        result.add(superclass);
        getSuperType(superclass, result);
    }

    /**
     * 获取所有接口
     *
     * @param type 类
     * @return 所有接口
     */
    public static void withInterface(Class<?> type, Consumer<Class<?>> consumer) {
        Set<Class<?>> result = new HashSet<>();
        loopInterfaces(type, result);
        for (Class<?> aClass : result) {
            consumer.accept(aClass);
        }
    }

    /**
     * 获取所有接口
     *
     * @param type 类
     * @return 所有接口
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> type) {
        Set<Class<?>> result = new HashSet<>();
        loopInterfaces(type, result);
        return result;
    }

    /**
     * 获取所有接口
     *
     * @param type   类
     * @param result 结果
     */
    private static void loopInterfaces(Class<?> type, Set<Class<?>> result) {
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            result.add(anInterface);
            loopInterfaces(anInterface, result);
        }
    }

    /**
     * 获取所有父类
     *
     * @param type 类
     * @return 所有接口
     */
    public static void withSuperType(Class<?> type, Consumer<Class<?>> consumer) {
        Set<Class<?>> result = new HashSet<>();
        getSuperType(type, result);
        for (Class<?> aClass : result) {
            consumer.accept(aClass);
        }
    }

    /**
     * 获取类加载器下的资源
     *
     * @param classLoader 类加载器
     * @return 资源
     */
    public static List<java.net.URL> classLoaderJarRoots(ClassLoader classLoader) {
        if (!(classLoader instanceof URLClassLoader)) {
            return Collections.emptyList();
        }

        //获取资源文件
        Enumeration<URL> enumeration = null;

        try {
            enumeration = classLoader.getResources("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<URL> rs = new LinkedList<>();

        while (enumeration.hasMoreElements()) {
            rs.add(enumeration.nextElement());
        }

        URL[] urls = ((URLClassLoader) classLoader).getURLs();
        rs.addAll(Arrays.asList(urls));
        //是系统加载器扫描 清单条目
        if (classLoader == ClassLoader.getSystemClassLoader()) {
            String javaClassPathProperty = System.getProperty(JAVA_CLASS_PATH);
            for (String path : StringUtils.delimitedListToStringArray(javaClassPathProperty,
                    System.getProperty(SYMBOL_LEFT_SLASH))) {
                try {
                    rs.add(new File(path).toURI().toURL());
                } catch (Exception ignore) {
                }
            }
        }

        return rs;

    }

    /**
     * 查询唯一类型值, 非唯一返回空
     *
     * @param args   参数
     * @param aClass 类型
     * @return
     */
    public static Object findOnlyOneValue(Collection<Object> args, Class<?> aClass) {
        return findOnlyOneValue(args.toArray(), aClass, 0);
    }

    /**
     * 查询唯一类型值, 非唯一返回空
     *
     * @param args   参数
     * @param aClass 类型
     * @param index
     * @return
     */
    public static Object findOnlyOneValue(Object[] args, Class<?> aClass, int index) {
        if (null == args) {
            return null;
        }
        aClass = fromPrimitive(aClass);

        List<Object> tpl = new LinkedList<>();
        for (Object arg : args) {
            if (null == arg || (null != arg && aClass.isAssignableFrom(arg.getClass()))) {
                tpl.add(arg);
            }
        }

        return CollectionUtils.find(tpl, index);
    }

    /**
     * 获取注解
     *
     * @param type           类型
     * @param annotationType 注解
     * @return 结果
     */
    public static <A extends Annotation> A getDeclaredAnnotation(Object type, Class<? extends A> annotationType) {
        if (null == annotationType) {
            return null;
        }

        if (type instanceof Class) {
            return ((Class<?>) type).getDeclaredAnnotation(annotationType);
        }

        if (type instanceof Method) {
            return ((Method) type).getDeclaredAnnotation(annotationType);
        }

        if (type instanceof Field) {
            return ((Field) type).getDeclaredAnnotation(annotationType);
        }

        if (type instanceof Constructor) {
            return ((Constructor) type).getDeclaredAnnotation(annotationType);
        }

        return null;
    }

    /**
     * 获取类的类加载器
     *
     * @param caller 类
     * @return ClassLoader
     */
    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        Preconditions.checkNotNull(caller);
        ClassLoader classLoader = caller.getClassLoader();
        return null == classLoader ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    /**
     * 是否子类
     *
     * @param type  类型
     * @param value 对象
     * @return 是否子类
     */
    public static boolean isAssignableValue(Class<?> type, Object value) {
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    /**
     * 是否子类
     *
     * @param lhsType 类型
     * @param rhsType 对象
     * @return 是否子类
     */
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = convertIfPrimitive(rhsType);
            return (lhsType == resolvedPrimitive);
        }
        Class<?> resolvedWrapper = convertIfPrimitive(rhsType);
        return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
    }

    /**
     * 获取方法名称与方法
     *
     * @param type      类型
     * @param predicate 过滤
     * @return 方法名称与方法
     */
    public static Map<String, Method> getMethodsByName(Class<?> type, Predicate<Method> predicate) {
        Map<String, Method> rs = new LinkedHashMap<>();
        doWithMethods(type, new SafeConsumer<Method>() {
            @Override
            public void safeAccept(Method method) throws Throwable {
                if (!predicate.test(method)) {
                    return;
                }

                rs.put(method.getName(), method);
                rs.put(NamingCase.toCamelUnderscore(method.getName()), method);
            }
        });
        return rs;
    }


    /**
     * 过滤类型
     *
     * @param sources 对象
     * @param limit   限制数量
     * @param type    类型
     * @param name    方法名称
     * @param index   参数位置
     * @param <T>     类型
     * @return 类型
     */
    public static void forFilterType(List<?> sources, TypeValue type, String name, int... index) {
        for (Object source : sources) {
            IndexValue indexValue = filterType(source, type);
            if (null != indexValue) {
                Class<?> aClass = source.getClass();
                ClassUtils.invokeMethodChain(source, aClass, name, createArgs(indexValue, index));
            }
        }
    }

    /**
     * 过滤类型
     *
     * @param preEvent 对象
     * @param limit    限制数量
     * @param type     类型
     * @param <T>      类型
     * @return 类型
     */
    public static IndexValue filterType(Object preEvent, TypeValue type) {
        IndexValue indexValue = new SimpleIndexValue();
        Type[] typeArguments = ClassUtils.getActualTypeArguments(preEvent.getClass());
        for (Type typeArgument : typeArguments) {
            if (typeArgument instanceof Class) {
                indexValue.add(new SimpleKeyValue((Class) typeArgument, type.get((Class) typeArgument)));
                continue;
            }
            indexValue.addAll(filterType(typeArgument, type).getAll());
        }


        return indexValue;
    }

    /**
     * 获取简单泛型
     *
     * @param declaredClass 类
     * @param <T>           类型
     * @return 泛型
     */
    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }

    /**
     * 参数
     *
     * @param method    方法
     * @param arguments 参数
     * @return 结果
     */
    public static Object[] toArgs(Method method, Object[] arguments) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] rs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            rs[i] = Converter.convertIfNecessary(arguments[i], parameterType);
        }
        return rs;
    }

    /**
     * 所有父类
     *
     * @param type 类型
     * @return 类型
     */
    public static Set<Class<?>> getAllType(Class<?> type) {
        Set<Class<?>> rs = new LinkedHashSet<>();
        getSuperType(type, rs);

        return rs;
    }

    /**
     * 获取真实类型
     *
     * @param returnType 类型
     * @return 类型
     */
    public static Class<?> getActualType(Class<?> returnType) {
        if (returnType.isArray()) {
            return forName(returnType.getTypeName().replace("[]", ""), returnType.getClassLoader());
        }
        return returnType;
    }


    static class SetAccessibleAction<T extends AccessibleObject> implements PrivilegedAction<T> {
        private final T obj;

        public SetAccessibleAction(T obj) {
            this.obj = obj;
        }

        @Override
        public T run() {
            obj.setAccessible(true);
            return obj;
        }

    }

}
