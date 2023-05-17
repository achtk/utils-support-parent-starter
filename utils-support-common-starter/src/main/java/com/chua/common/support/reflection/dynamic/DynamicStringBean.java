package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.reflection.MethodStation;
import com.chua.common.support.reflection.dynamic.attribute.AnnotationAttribute;
import com.chua.common.support.reflection.dynamic.attribute.ConstructAttribute;
import com.chua.common.support.reflection.dynamic.attribute.FieldAttribute;
import com.chua.common.support.reflection.dynamic.attribute.MethodAttribute;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.JavassistUtils;
import com.chua.common.support.utils.StringUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 动态对象
 *
 * @author ch
 */
public class DynamicStringBean implements DynamicBean {

    private final Object bean;
    private Class<?> type;

    protected DynamicStringBean(Object bean, Class<?> type) {
        this.bean = bean;
        this.type = type;
    }


    public static <T> DynamicBeanBuilder<T> newBuilder() {
        return new DynamicStringBeanBuilder<>();
    }


    public static class DynamicStringBeanBuilder<T> extends AbstractDynamicBeanStandardBuilder<T> {

        @Override
        public DynamicBean build() {
            if (ClassUtils.isPresent(name)) {
                Object forObject = ClassUtils.forObject(name);
                if (null != forObject) {
                    //渲染值
                    doAnalysisFieldValue(forObject, fieldsInfos);
                    return new DynamicStringBean(forObject, forObject.getClass());
                }
            }

            List<Throwable> exceptions = new LinkedList<>();

            ClassPool classPool = new ClassPool(true);
            classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

            CtClass clazz;
            if (isClass) {
                clazz = classPool.makeClass(name);
            } else {
                clazz = classPool.makeInterface(name);
            }
            //渲染包
            doAnalysisPackages(classPool);
            //渲染接口
            doAnalysisInterface(classPool, clazz, exceptions);
            //渲染超类
            doAnalysisSuperType(classPool, clazz, exceptions);
            //渲染类
            doAnalysisTypeAnnotation(clazz);
            //渲染字段
            doAnalysisFields(classPool, clazz, exceptions);
            //渲染方法
            doAnalysisMethods(classPool, clazz, exceptions);
            //渲染构造
            doAnalysisConstructs(clazz, exceptions);

            Class<T> tClass = renderType(classPool, clazz);
            //生成对象
            Object render = render(classPool, tClass);

            return new DynamicStringBean(render, tClass);
        }

        /**
         * 生成对象
         *
         * @param classPool 类池
         * @param clazz     类
         * @return 对象
         */
        private Class<T> renderType(ClassPool classPool, CtClass clazz) {
            //类加载器
            ClassLoader callerClassLoader = ClassUtils.getCallerClassLoader(this.getClass());
            //渲染对象
            return (Class<T>) JavassistUtils.loader(clazz, classPool, callerClassLoader);
        }

        /**
         * 生成对象
         *
         * @param classPool 类池
         * @param type      类
         * @return 对象
         */
        private Object render(ClassPool classPool, Class<T> type) {

            if (!isClass) {
                return type;
            }
            //结果
            Object forObject = ClassUtils.forObject(type);
            if (null == forObject) {
                return null;
            }
            //渲染值
            doAnalysisFieldValue(forObject, fieldsInfos);
            return forObject;
        }


        /**
         * 渲染构造
         *
         * @param clazz      类
         * @param exceptions 异常
         */
        private void doAnalysisConstructs(CtClass clazz, List<Throwable> exceptions) {
            if (!isClass) {
                return;
            }

            for (ConstructAttribute constructInfo : constructInfos) {
                try {
                    CtConstructor constructor = CtNewConstructor.make(
                            JavassistUtils.toCtClass(constructInfo.getArgTypes()),
                            JavassistUtils.toCtClass(constructInfo.getExceptionTypes()),
                            clazz);
                    constructor.setModifiers(constructInfo.getModifiers());
                    doAnalysisAnnotation(constructor, constructInfo.getAnnotationTypes(), clazz);
                    clazz.addConstructor(constructor);
                } catch (Throwable e) {
                    exceptions.add(e);
                }
            }
        }

        /**
         * 渲染方法
         *
         * @param classPool  类池
         * @param clazz      类
         * @param exceptions 异常
         */
        private void doAnalysisMethods(ClassPool classPool, CtClass clazz, List<Throwable> exceptions) {
            for (MethodAttribute methodInfo : methodInfos) {
                try {
                    CtMethod ctMethod;
                    if (isClass) {
                        ctMethod = CtNewMethod.make(
                                classPool.get(methodInfo.getReturnType()),
                                methodInfo.getName(),
                                JavassistUtils.toCtClass(methodInfo.getArgTypes()),
                                JavassistUtils.toCtClass(methodInfo.getExceptionTypes()),
                                methodInfo.getBody(),
                                clazz);

                    } else {
                        ctMethod = CtNewMethod.abstractMethod(
                                classPool.get(methodInfo.getReturnType()),
                                methodInfo.getName(),
                                JavassistUtils.toCtClass(methodInfo.getArgTypes()),
                                JavassistUtils.toCtClass(methodInfo.getExceptionTypes()),
                                clazz);
                    }
                    ctMethod.setModifiers(methodInfo.getModifiers());
                    doAnalysisAnnotation(ctMethod, methodInfo.getAnnotationTypes(), clazz);
                    clazz.addMethod(ctMethod);
                } catch (Throwable e) {
                    exceptions.add(e);
                }
            }
        }

        /**
         * 渲染字段
         *
         * @param clazz 类
         */
        private void doAnalysisTypeAnnotation(CtClass clazz) {
            doAnalysisTypeAnnotation(annotations.toArray(new AnnotationAttribute[0]), clazz);
        }


        /**
         * 渲染字段
         *
         * @param classPool  类池
         * @param clazz      类
         * @param exceptions 异常
         */
        private void doAnalysisFields(ClassPool classPool, CtClass clazz, List<Throwable> exceptions) {
            if (!isClass) {
                return;
            }
            for (FieldAttribute fieldsInfo : fieldsInfos) {
                try {
                    CtField ctField = new CtField(classPool.get(fieldsInfo.getType()), fieldsInfo.getName(), clazz);
                    ctField.setModifiers(fieldsInfo.getModifiers());
                    doAnalysisAnnotation(ctField, fieldsInfo.getAnnotationTypes(), clazz);
                    clazz.addField(ctField);
                } catch (Throwable e) {
                    exceptions.add(e);
                }
            }
        }

        /**
         * 选阿然注解
         *
         * @param ct              方式
         * @param annotationTypes 注解类型
         * @param clazz           类
         */
        private void doAnalysisAnnotation(Object ct, AnnotationAttribute[] annotationTypes, CtClass clazz) {
            if (ct instanceof CtField) {
                doAnalysisFieldAnnotation((CtField) ct, annotationTypes, clazz);
                return;
            }

            if (ct instanceof CtMethod) {
                doAnalysisMethodAnnotation((CtMethod) ct, annotationTypes, clazz);
                return;
            }

            if (ct instanceof CtConstructor) {
                doAnalysisConstructAnnotation((CtConstructor) ct, annotationTypes, clazz);
                return;
            }

            doAnalysisTypeAnnotation(annotationTypes, clazz);
        }

        /**
         * 渲染类注解
         *
         * @param annotationTypes 注解类型
         * @param clazz           类
         */
        private void doAnalysisTypeAnnotation(AnnotationAttribute[] annotationTypes, CtClass clazz) {
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            for (AnnotationAttribute annotationType : annotationTypes) {
                Annotation annotation1 = new Annotation(annotationType.getName(), constPool);
                doAnalysisAnnotationValue(annotation1, null, constPool, annotationType.getParams());
                annotationsAttribute.addAnnotation(annotation1);
            }
            classFile.addAttribute(annotationsAttribute);
        }

        /**
         * 渲染构造注解
         *
         * @param ctConstructor   字段
         * @param annotationTypes 注解类型
         * @param clazz           类
         */
        private void doAnalysisConstructAnnotation(CtConstructor ctConstructor, AnnotationAttribute[] annotationTypes, CtClass clazz) {
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            javassist.bytecode.MethodInfo fieldInfo = ctConstructor.getMethodInfo();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

            for (AnnotationAttribute annotationType : annotationTypes) {
                Annotation annotation1 = new Annotation(annotationType.getName(), constPool);
                doAnalysisAnnotationValue(annotation1, Constructor.class, constPool, annotationType.getParams());
                annotationsAttribute.addAnnotation(annotation1);
            }
            fieldInfo.addAttribute(annotationsAttribute);
            classFile.addAttribute(annotationsAttribute);
        }

        /**
         * 渲染方法注解
         *
         * @param ctMethod        字段
         * @param annotationTypes 注解类型
         * @param clazz           类
         */
        private void doAnalysisMethodAnnotation(CtMethod ctMethod, AnnotationAttribute[] annotationTypes, CtClass clazz) {
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            javassist.bytecode.MethodInfo fieldInfo = ctMethod.getMethodInfo();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

            for (AnnotationAttribute annotationType : annotationTypes) {
                Annotation annotation1 = new Annotation(annotationType.getName(), constPool);
                doAnalysisAnnotationValue(annotation1, Method.class, constPool, annotationType.getParams());
                annotationsAttribute.addAnnotation(annotation1);
            }
            fieldInfo.addAttribute(annotationsAttribute);
            classFile.addAttribute(annotationsAttribute);
        }

        /**
         * 渲染字段注解
         *
         * @param ctField         字段
         * @param annotationTypes 注解类型
         * @param clazz           类
         */
        private void doAnalysisFieldAnnotation(CtField ctField, AnnotationAttribute[] annotationTypes, CtClass clazz) {
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            javassist.bytecode.FieldInfo fieldInfo = ctField.getFieldInfo();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

            for (AnnotationAttribute annotationType : annotationTypes) {
                Annotation annotation1 = new Annotation(annotationType.getName(), constPool);
                doAnalysisAnnotationValue(annotation1, Field.class, constPool, annotationType.getParams());
                annotationsAttribute.addAnnotation(annotation1);
            }
            fieldInfo.addAttribute(annotationsAttribute);
            classFile.addAttribute(annotationsAttribute);
        }

        /**
         * 添加注解值
         *
         * @param annotation 注解
         * @param type       注解方式
         * @param constPool  常量池
         * @param params
         */
        private void doAnalysisAnnotationValue(Annotation annotation, Class<?> type, ConstPool constPool, Map<String, Object> params) {
            if (null == annotationFunction) {
                return;
            }

            Map<String, Object> apply = params;

            for (Map.Entry<String, Object> entry : apply.entrySet()) {
                Object value = entry.getValue();
                if (null == value || "".equals(value)) {
                    continue;
                }
                MemberValue memberValue = JavassistUtils.getMemberValue(value, constPool);
                if (null == memberValue) {
                    continue;
                }
                annotation.addMemberValue(entry.getKey(), memberValue);
            }

        }

        /**
         * 渲染超类
         *
         * @param classPool  类池
         * @param clazz      类
         * @param exceptions 异常
         */
        private void doAnalysisSuperType(ClassPool classPool, CtClass clazz, List<Throwable> exceptions) {
            if (StringUtils.isNullOrEmpty(superType) || !isClass) {
                return;
            }

            try {
                clazz.setSuperclass(classPool.get(superType));
            } catch (CannotCompileException | NotFoundException e) {
                exceptions.add(e);
            }
        }

        /**
         * 渲染接口
         *
         * @param classPool  类池
         * @param clazz      类
         * @param exceptions 异常
         */
        private void doAnalysisInterface(ClassPool classPool, CtClass clazz, List<Throwable> exceptions) {
            for (String anInterface : interfaces) {
                try {
                    clazz.addInterface(classPool.get(anInterface));
                } catch (NotFoundException e) {
                    exceptions.add(e);
                }
            }
        }

        /**
         * 渲染包
         *
         * @param classPool 类池
         */
        private void doAnalysisPackages(ClassPool classPool) {
            for (String aPackage : packages) {
                classPool.importPackage(aPackage);
            }

            for (String anInterface : interfaces) {
                classPool.importPackage(anInterface);
            }

            for (AnnotationAttribute annotation : annotations) {
                classPool.importPackage(annotation.getType());
            }

            for (FieldAttribute fieldInfo : fieldsInfos) {
                classPool.importPackage(fieldInfo.getType());

                Object value = fieldInfo.getValue();
                if (null == value) {
                    continue;
                }

                classPool.importPackage(value.getClass().getTypeName());

            }

            for (MethodAttribute methodInfo : methodInfos) {
                classPool.importPackage(methodInfo.getReturnType());

                for (String argType : methodInfo.getArgTypes()) {
                    classPool.importPackage(argType);
                }

                for (String s : methodInfo.getExceptionTypes()) {
                    classPool.importPackage(s);
                }

            }
        }
    }

    @Override
    public <T> T createBean(Class<T> type) {
        if (null == type || type.isAssignableFrom(this.type)) {
            return (T) bean;
        }
        return ProxyUtils.newProxy(type, (obj, method, args, proxy, plugins) -> {
            return MethodStation.of(bean).invoke(method.getName(), args);
        });
    }

    @Override
    public <T> Class<T> createType(Class<T> type) {
        return (Class<T>) this.type;
    }


    /**
     * 渲染字段值
     *
     * @param forObject   对象
     * @param fieldsInfos
     */
    public static void doAnalysisFieldValue(Object forObject, List<FieldAttribute> fieldsInfos) {
        Class<?> aClass = forObject.getClass();
        for (FieldAttribute fieldInfo : fieldsInfos) {
            Object value = fieldInfo.getValue();
            if (null == value) {
                continue;
            }
            try {
                Field declaredField = aClass.getDeclaredField(fieldInfo.getName());
                if (null == declaredField) {
                    continue;
                }
                declaredField.setAccessible(true);
                declaredField.set(forObject, value);
            } catch (Exception ignored) {
            }
        }
    }

}
