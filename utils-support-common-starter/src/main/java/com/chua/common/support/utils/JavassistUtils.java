package com.chua.common.support.utils;


import javassist.*;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;
import static com.chua.common.support.constant.CommonConstant.EMPTY_CLASS;


/**
 * javassist工具类
 *
 * @author CH
 */
public class JavassistUtils {
    private static final CtClass[] EMPTY = new CtClass[0];
    private static final String[] EMPTY_STRING_ARRAY = EMPTY_ARRAY;
    private static final String JAVASSIST = "$Javassist";

    /**
     * 非匿名内部类处理
     *
     * @param bean           对象
     * @param consumer       消费者
     * @param interfaceClass 接口
     * @return 类
     */
    public static CtClass createClass(Object bean, BiFunction<String, List<Class<?>>, String> consumer, Class<?>[] interfaceClass) throws NotFoundException {
        String name = bean.getClass().getName();
        ClassPool classPool = getClassPool();
        CtClass newCtClass = classPool.get(name);
        newCtClass.setName(name + JAVASSIST);
        newCtClass.setModifiers(Modifier.PUBLIC);

        for (Class<?> aClass : interfaceClass) {
            try {
                CtClass interfaceCtClass = classPool.get(aClass.getName());
                newCtClass.addInterface(interfaceCtClass);
                if (null != consumer) {
                    repairMethods(newCtClass, consumer, interfaceCtClass, classPool);
                }
            } catch (NotFoundException e) {
                e.getMessage();
            }
        }
        newCtClass.setModifiers(Modifier.PUBLIC);

        return newCtClass;
    }

    /**
     * 比较接口方法
     *
     * @param ctClass        源对象
     * @param consumer       方法处理
     * @param interfaceClass 接口
     * @param classPool      类池
     */
    private static void repairMethods(CtClass ctClass, BiFunction<String, List<Class<?>>, String> consumer, CtClass interfaceClass, ClassPool classPool) {
        CtMethod[] methods = interfaceClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            CtMethod statelessMethods = null;
            CtMethod method = methods[i];
            try {
                CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                if (null != ctMethod) {
                    continue;
                }
                statelessMethods = generateStatelessMethods(method, ctClass, classPool);
            } catch (NotFoundException ignore) {
                try {
                    statelessMethods = generateStatelessMethods(method, ctClass, classPool);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                String apply = consumer.apply(statelessMethods.getName(), toList(toClass(statelessMethods.getParameterTypes())));
                if (!StringUtils.isNullOrEmpty(apply) && !apply.endsWith(";")) {
                    apply += ";";
                }
                statelessMethods.setBody(apply);
                statelessMethods.setModifiers(Modifier.PUBLIC);
                ctClass.addMethod(statelessMethods);
            } catch (NotFoundException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 集合转数组
     *
     * @param source 集合
     * @param <T>    类型
     * @return 数组
     */
    private static <T> List<Class<?>> toList(final Class<?>[] source) {
        if (null == source) {
            return Collections.emptyList();
        }
        return CollectionUtils.newArrayList(source);
    }

    /**
     * 方法拷贝(无状态)
     *
     * @param ctMethod  方法
     * @param ctClass   生成的类
     * @param classPool 类池
     * @return 方法
     */
    private static CtMethod generateStatelessMethods(final CtMethod ctMethod, CtClass ctClass, ClassPool classPool) throws NotFoundException {
        CtClass returnType = ctMethod.getReturnType();
        CtClass[] parameters = ctMethod.getParameterTypes();
        CtClass[] newParameters = new CtClass[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            CtClass parameter = parameters[i];
            newParameters[i] = classPool.get(parameter.getName());
        }
        CtMethod ctMethod1 = new CtMethod(returnType, ctMethod.getName(), newParameters, ctClass);
        ctMethod1.setModifiers(Modifier.PUBLIC);
        return ctMethod1;
    }

    /**
     * 获取MemberValue
     *
     * @param value     原始值
     * @param constPool 对象池
     * @return MemberValue
     */
    @SuppressWarnings("all")
    public static MemberValue getMemberValue(Object value, ConstPool constPool) {

        if (null == value) {
            return null;
        }
        if (value instanceof Integer) {
            return new IntegerMemberValue(constPool, (Integer) value);
        }

        if (value instanceof Long) {
            return new LongMemberValue((Long) value, constPool);
        }

        if (value instanceof Double) {
            return new DoubleMemberValue((Double) value, constPool);
        }

        if (value instanceof Float) {
            return new FloatMemberValue((Float) value, constPool);
        }

        if (value instanceof Boolean) {
            return new BooleanMemberValue((Boolean) value, constPool);
        }

        if (value instanceof Byte) {
            return new ByteMemberValue((Byte) value, constPool);
        }

        if (value instanceof Class) {
            return new ClassMemberValue(((Class) value).getName(), constPool);
        }

        if (value instanceof Short) {
            return new ShortMemberValue((Short) value, constPool);
        }

        if (value instanceof Character) {
            return new CharMemberValue((Character) value, constPool);
        }
        if (value instanceof String) {
            return new StringMemberValue(value.toString(), constPool);
        }

        if (value instanceof Enum) {
//            Method method = MethodStation.of(value).getMethod("valueOf");
//            method.setAccessible(true);
//            Object invoke = method.invoke(null, ((Enum<?>) value).name());

            EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
            enumMemberValue.setType(value.getClass().getName());
            enumMemberValue.setValue(((Enum<?>) value).name());
            return enumMemberValue;
        }

        if (value.getClass().isArray()) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
            Object[] objects = (Object[]) value;
            MemberValue[] memberValues = new MemberValue[objects.length];
            for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
                Object object = objects[i];
                memberValues[i] = getMemberValue(object, constPool);
            }
            arrayMemberValue.setValue(memberValues);
            return arrayMemberValue;
        }
        return null;
    }

    /**
     * 获取 classPool
     *
     * @return classPool
     */
    public static ClassPool getClassPool() {
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        return classPool;
    }

    /**
     * CtClass -> Object
     *
     * @param ctClass   ctClass
     * @param classPool classPool
     * @return 对象
     */
    public static Object toEntity(CtClass ctClass, ClassPool classPool) throws Exception {
        try {
            return ClassUtils.forObject(ctClass.toClass());
        } catch (Throwable e) {
            //类加载器
            Loader classLoader = new Loader(ClassUtils.getDefaultClassLoader(), classPool);
            classLoader.addTranslator(classPool, new Translator() {
                @Override
                public void onLoad(ClassPool pool, String classname) {

                }

                @Override
                public void start(ClassPool pool) {

                }
            });
            ctClass.defrost();
            Class<?> aClass = classLoader.loadClass(ctClass.toClass().getName());
            return ClassUtils.forObject(aClass);
        }
    }

    /**
     * 类转Class类
     *
     * @param objects 类
     * @return ct类
     */
    public static Class<?>[] toClass(Object[] objects) {
        if (null == objects) {
            return new Class[]{null};
        }
        if (objects.length == 0) {
            return EMPTY_CLASS;
        }
        List<Class<?>> ctClassList = new ArrayList<>(objects.length);
        for (Object obj : objects) {
            if (obj instanceof String) {
                ctClassList.add(ClassUtils.forName(obj.toString()));
            } else {
                if (null == obj) {
                    ctClassList.add(Object.class);
                } else if (obj instanceof Map) {
                    ctClassList.add(Map.class);
                } else if (obj instanceof List) {
                    ctClassList.add(List.class);
                } else {
                    ctClassList.add(Object.class);
                }
            }
        }
        return ctClassList.toArray(EMPTY_CLASS);
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass toCtClass(ClassPool classPool, Class<?> classes) throws Exception {
        if (null == classes) {
            return null;
        }
        return classPool.get(classes.getName());
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass toCtClass(Class<?> classes) throws Exception {
        return toCtClass(getClassPool(), classes);
    }

    /**
     * 类转Ct类
     *
     * @param classes   类
     * @param classPool classPool
     * @return ct类
     */
    public static CtClass toCtClass(Class<?> classes, ClassPool classPool) throws Exception {
        return toCtClass(classPool, classes);
    }

    /**
     * 类转Ct类
     *
     * @param classes   类
     * @param classPool 池
     * @return ct类
     */
    public static CtClass toCtClass(String classes, ClassPool classPool) throws Exception {
        if (null == classes) {
            return null;
        }
        return classPool.get(classes);
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass toCtClass(String classes) throws Exception {
        return toCtClass(classes, getClassPool());
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass[] toCtClass(ClassPool classPool, Class<?>[] classes) {
        if (null == classes || classes.length == 0) {
            return EMPTY;
        }
        List<CtClass> ctClassList = new ArrayList<>(classes.length);
        for (Class<?> aClass : classes) {
            try {
                ctClassList.add(toCtClass(classPool, aClass));
            } catch (Exception e) {
                return EMPTY;
            }
        }
        return ctClassList.toArray(EMPTY);
    }

    /**
     * 类转Ct类
     *
     * @param classes   类
     * @param classPool classPool
     * @return ct类
     */
    public static CtClass[] toCtClass(Class<?>[] classes, ClassPool classPool) {
        if (null == classes || classes.length == 0) {
            return EMPTY;
        }
        List<CtClass> ctClassList = new ArrayList<>(classes.length);
        for (Class<?> aClass : classes) {
            try {
                ctClassList.add(toCtClass(aClass, classPool));
            } catch (Exception e) {
                return EMPTY;
            }
        }
        return ctClassList.toArray(EMPTY);
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass[] toCtClass(Class<?>[] classes) {
        return toCtClass(classes, getClassPool());
    }

    /**
     * 类转Ct类
     *
     * @param classes   类
     * @param classPool 类池
     * @return ct类
     */
    public static CtClass[] toCtClass(String[] classes, ClassPool classPool) {
        if (null == classes || classes.length == 0) {
            return EMPTY;
        }
        List<CtClass> ctClassList = new ArrayList<>(classes.length);
        for (String aClass : classes) {
            try {
                ctClassList.add(toCtClass(aClass, classPool));
            } catch (Exception e) {
                return EMPTY;
            }
        }
        return ctClassList.toArray(EMPTY);
    }

    /**
     * 类转Ct类
     *
     * @param classes 类
     * @return ct类
     */
    public static CtClass[] toCtClass(String[] classes) {
        return toCtClass(classes, getClassPool());
    }

    /**
     * ctClass转名称
     *
     * @param parameterTypes 类型
     * @return 名称
     */
    public static String[] toTypeName(CtClass[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return EMPTY_STRING_ARRAY;
        }

        String[] result = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            CtClass parameterType = parameterTypes[i];
            result[i] = parameterType.getName();
        }

        return result;

    }


    /**
     * 渲染类对象{注意: {@link NullPointerException}}
     *
     * @param ctClass           Ct类
     * @param classPool         类池
     * @param callerClassLoader 加载器
     * @return 类
     * @see NullPointerException
     * @see ClassLoader
     * @see Class
     */
    public static Class<?> loader(CtClass ctClass, ClassPool classPool, ClassLoader callerClassLoader) {
        try {
            return callerClassLoader.loadClass(ctClass.getName());
        } catch (Exception e) {
            try {
                Loader classLoader = new Loader(callerClassLoader, classPool);
                classLoader.addTranslator(classPool, new Translator() {
                    @Override
                    public void onLoad(ClassPool pool, String classname) {

                    }

                    @Override
                    public void start(ClassPool pool) {

                    }
                });
                return classLoader.loadClass(ctClass.toClass().getName());
            } catch (Exception ignored) {
            }
            return null;
        }
    }

}
