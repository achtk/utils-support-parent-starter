package com.chua.common.support.lang.proxy;

import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * javassist代理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class JavassistProxyFactory<T> implements ProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JavassistProxyFactory();

    @Override
    @SneakyThrows
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept) {
        ClassPool classPool = JavassistUtils.getClassPool();
        classPool.importPackage(intercept.getClass().getTypeName());
        String name = "intercept";

        CtClass ctClass = classPool.makeClass("com." + target.getTypeName() + "#" + System.currentTimeMillis());
        CtField ctField = new CtField(classPool.get(intercept.getClass().getTypeName()), name, ctClass);
        ctClass.addField(ctField);
        ctClass.addMethod(new CtNewMethod().setter("setIntercept", ctField));
        ctClass.addMethod(new CtNewMethod().getter("getIntercept", ctField));
        Method[] methods = target.getDeclaredMethods();
        if (target.isInterface()) {
            ctClass.addInterface(classPool.get(target.getTypeName()));
            analyInterface(ctClass, classPool, methods);
        } else {
            ctClass.setSuperclass(classPool.get(target.getTypeName()));
            analyAbstract(ctClass, classPool, methods);
        }


        Object toEntity = JavassistUtils.toEntity(ctClass, classPool);
        if (null == toEntity) {
            return null;
        }

        ClassUtils.setFieldValue(name, intercept, toEntity);

        return (T) toEntity;
    }

    /**
     * 接口方法
     *
     * @param ctClass   类
     * @param classPool 类池
     * @param methods   方法
     */
    private void analyInterface(CtClass ctClass, ClassPool classPool, Method[] methods) throws Exception {
        for (Method method : methods) {
            if (method.isDefault()) {
                continue;
            }

            ctClass.addMethod(CtNewMethod.make(
                    method.getModifiers(),
                    JavassistUtils.toCtClass(method.getReturnType(), classPool),
                    method.getName(),
                    JavassistUtils.toCtClass(method.getParameterTypes(), classPool),
                    JavassistUtils.toCtClass(method.getExceptionTypes(), classPool),
                    "{ if(null == intercept) return null; return intercept.execute(\"" + method.getName() + "\", $args);}",
                    ctClass
            ));
        }
    }

    /**
     * 抽象方法
     *
     * @param ctClass   类
     * @param classPool 类池
     * @param methods   方法
     */
    private void analyAbstract(CtClass ctClass, ClassPool classPool, Method[] methods) throws Exception {
        for (Method method : methods) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                continue;
            }

            ctClass.addMethod(CtNewMethod.make(
                    method.getModifiers(),
                    JavassistUtils.toCtClass(method.getReturnType(), classPool),
                    method.getName(),
                    JavassistUtils.toCtClass(method.getParameterTypes(), classPool),
                    JavassistUtils.toCtClass(method.getExceptionTypes(), classPool),
                    "{ if(null == intercept) return null;return intercept.execute(\"" + method.getName() + "\", $args);}",
                    ctClass
            ));
        }
    }
}
