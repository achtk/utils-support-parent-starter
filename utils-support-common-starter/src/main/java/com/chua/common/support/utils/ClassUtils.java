package com.chua.common.support.utils;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.converter.Converter;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import static com.chua.common.support.constant.CommonConstant.*;

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
     * 是否包含类
     *
     * @param className 类名
     * @return 类加载器是否包含该类, 包含返回true
     */
    public static boolean isPresent(final String className) {
        return isPresent(className, null);
    }

    /**
     * 是否包含类
     *
     * @param className   类名
     * @param classLoader 类加载器
     * @return 类加载器是否包含该类, 包含返回true
     */
    public static boolean isPresent(final String className, final ClassLoader classLoader) {
        try {
            final Class<?> aClass = forName(className, classLoader);
            return null != aClass;
        } catch (Throwable ex) {
            return false;
        }
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
     * @param name 类名称
     * @return 返回转换后的 Class
     */
    public static Class<?> toClassConfident(String name) {
        try {
            return Class.forName(name, false, getDefaultClassLoader());
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
     * 字符转类
     *
     * @param name       名称
     * @param returnType 返回类型
     * @return 类
     * @throws LinkageError           ex 连接异常
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
     * @throws LinkageError           连接异常
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
                return aClass;
            }
        }

        Class<?> aClass = object.getClass();
        if (!Proxy.isProxyClass(aClass)) {
            return aClass;
        }

        String toString = object.toString();
        if (!toString.contains("$Proxy")) {
            Class<?> aClass1 = forName(StringUtils.removeSuffixContains(toString.replace("@", ""), "("), object.getClass().getClassLoader());
            return null == aClass1 ? void.class : aClass1;
        }
        return null;
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
}
