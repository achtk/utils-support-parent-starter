package com.chua.agent.support.utils;

import com.chua.agent.support.reflectasm.FieldAccess;
import com.chua.agent.support.reflectasm.MethodAccess;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 类工具类
 *
 * @author CH
 * @since 2021-08-18
 */
public class ClassUtils {
    private static final char SYMBOL_DOT_CHAR = '.';
    private static final Map<String, Class<?>> BASE_NAME_TYPE = new ConcurrentHashMap<>();

    /**
     * 获取类的类加载器
     *
     * @param caller 类
     * @return ClassLoader
     */
    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        ClassLoader classLoader = caller.getClassLoader();
        return null == classLoader ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    /**
     * 通过包+类名获取类
     *
     * @param packages  包
     * @param className 类名
     * @return 类
     */
    public static Class<?> forName(String[] packages, String className) {
        try {
            return forNameNoClassLoader(className);
        } catch (ClassNotFoundException e) {
            if (null == packages) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            for (String pkg : packages) {
                try {
                    return forNameNoClassLoader(pkg + "." + className);
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
        return null;
    }

    /**
     * 通过默认类加载器转化类名为类
     *
     * @param className 类名
     * @return 类
     * @throws ClassNotFoundException ClassNotFoundException
     * @see #getDefaultClassLoader()
     */
    private static Class<?> forNameNoClassLoader(String className) throws ClassNotFoundException {
        Class<?> aClass = BASE_NAME_TYPE.get(className);
        if (null != aClass) {
            return aClass;
        }

        try {
            return arrayForName(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className, false, getDefaultClassLoader());
            } catch (Exception e1) {
                if (className.indexOf(SYMBOL_DOT_CHAR) != -1) {
                    throw e;
                }
                try {
                    return arrayForName("java.lang." + className);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 获取默认类加载器
     * <p>
     * 默认获取当前线程的类加载器<code>Thread.currentThread().getContextClassLoader()</code>
     * <p>{@link Thread#currentThread()#getDefaultClassLoader()}</p>
     * </p>
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
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignored) {
                }
            }
        }
        return cl;
    }

    /**
     * 获取数组类类
     *
     * @param className 数组类类名
     * @return 类
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static Class<?> arrayForName(String className) throws ClassNotFoundException {
        return Class.forName(className.endsWith("[]")
                ? "[L" + className.substring(0, className.length() - 2) + ";"
                : className, true, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 获取简单名字
     * <pre>
     *     ClassHelper.getSimpleClassName("java.lang.String") = String
     * </pre>
     *
     * @param qualifiedName 方法名
     * @return 简单名字
     */
    public static String getSimpleClassName(String qualifiedName) {
        if (null == qualifiedName) {
            return null;
        }

        int i = qualifiedName.lastIndexOf('.');
        return i < 0 ? qualifiedName : qualifiedName.substring(i + 1);
    }

    /**
     * 获取map
     *
     * @param entity 实体
     * @return map
     */
    public static Map asMap(Object entity) {
        if (entity instanceof Map) {
            return (Map) entity;
        }
        Map<String, Object> result = new HashMap<>();
        Class<?> aClass = entity.getClass();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(aClass);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (null == readMethod || Modifier.isNative(readMethod.getModifiers())) {
                continue;
            }
            readMethod.setAccessible(true);
            try {
                result.put(propertyDescriptor.getName(), readMethod.invoke(entity));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    /*
     * 取得某一类所在包的所有类名 不含迭代
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        //将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {

        //第一个class类的集合
        Map<String, Class<?>> classes = new HashMap<>();
        //是否循环迭代
        boolean recursive = true;
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代下去
            while (dirs.hasMoreElements()) {
                //获取下一个元素
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    //如果是jar包文件
                    //定义一个JarFile
                    JarFile jar;
                    try {
                        //获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        //从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        //同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //如果是以/开头的
                            if (name.charAt(0) == '/') {
                                //获取后面的字符串
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    //获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    //如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            Class<?> aClass = Class.forName(packageName + '.' + className);
                                            //添加到classes
                                            classes.put(aClass.getName(), aClass);
                                        } catch (Throwable ignored) {
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(classes.values());
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Map<String, Class<?>> classes) {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    String name = packageName + '.' + className;
                    //添加到集合中去
                    classes.put(name, Class.forName(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static Map<Class<?>, FieldAccess> cache = new ConcurrentHashMap<>();
    static Map<Class<?>, MethodAccess> methodAccessMap = new ConcurrentHashMap<>();

    /**
     * 获取对象
     *
     * @param name   名称
     * @param object 对象
     * @return 结果
     */
    public static Object getObject(String name, Object object) {
        FieldAccess fieldAccess = cache.computeIfAbsent(object.getClass(), aClass -> FieldAccess.get(aClass));

        String[] fieldNames = fieldAccess.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            if (name.equalsIgnoreCase(fieldName)) {
                return getObject(i, object);
            }
        }
        return null;
    }

    /**
     * 获取对象
     *
     * @param i      索引
     * @param object 对象
     * @return 结果
     */
    public static Object getObject(int i, Object object) {
        FieldAccess fieldAccess = cache.computeIfAbsent(object.getClass(), aClass -> FieldAccess.get(aClass));

        Field field = fieldAccess.getFields()[i];
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 调用方法
     *
     * @param name 方法
     * @param bean bean
     */
    public static Object invoke(String name, Object bean, Object... args) {
        try {
            return invoke(name, bean.getClass(), bean, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 调用方法
     *
     * @param name 方法
     * @param type 类型
     * @param bean bean
     */
    public static Object invoke(String name, Class<?> type, Object bean, Object... args) {
        MethodAccess methodAccess = methodAccessMap.computeIfAbsent(type, aClass -> MethodAccess.get(type));

        List<String> collect = Arrays.stream(methodAccess.getMethodNames()).filter(name::equals).collect(Collectors.toList());
        if (collect.size() == 1) {
            int index = methodAccess.getIndex(name);
            return methodAccess.invoke(bean, index, args);
        }

        String[] methodNames = methodAccess.getMethodNames();
        for (int i = 0; i < methodNames.length; i++) {
            String methodName = methodNames[i];
            if (!methodName.equals(name)) {
                continue;
            }
            Class[] parameterType = methodAccess.getParameterTypes()[i];
            if (parameterType.length != args.length) {
                continue;
            }

            return methodAccess.invoke(bean, i, args);

        }
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

        if (object instanceof String && ((String) object).contains(".")) {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(object.toString());
            } catch (ClassNotFoundException e) {
                return object.getClass();
            }
            if (null != aClass) {
                return aClass;
            }
        }

        return object.getClass();
    }

    public static void setField(Class<?> aClass, Object newInstance, String o, Object agentServlet) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = aClass.getDeclaredField(o);
        declaredField.setAccessible(true);
        declaredField.set(newInstance, agentServlet);

    }
}
