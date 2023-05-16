package com.chua.common.support.reflection.marker;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.describe.describe.*;
import com.chua.common.support.proxy.BridgingMethodIntercept;
import com.chua.common.support.proxy.ProxyUtils;
import com.chua.common.support.utils.*;
import com.google.common.base.Strings;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.MemberValue;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;
import static com.chua.common.support.constant.CommonConstant.EMPTY_CLASS;


/**
 * 构建起
 *
 * @author CH
 */
public class AppendMarker implements Marker {
    private final Object entity;
    private final List<String> packages = new LinkedList<>();
    private final List<Class<?>> interfaces = new LinkedList<>();
    private Class<?> superType;
    private String name;
    private Class<Object> target;
    protected final List<MethodDescribe> methodDescribes = new LinkedList<>();
    private final List<FieldDescribe> fieldDescribes = new LinkedList<>();
    private AnnotationDescribe[] annotationDescribes;

    public AppendMarker(Object entity) {
        this.entity = entity;
    }

    @Override
    public Class<?>[] findAllClassesThatExtendsOrImplements() {
        return new Class[0];
    }

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public Bench createBench(MethodDescribe methodDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(ConstructDescribe constructDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(FieldDescribe fieldDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(String name, String[] parameterTypes) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createAttributeBench(String name) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Marker annotationType(AnnotationDescribe... annotationDescribes) {
        this.annotationDescribes = annotationDescribes;
        return this;
    }

    @Override
    public Marker imports(String... packages) {
        this.packages.addAll(Arrays.asList(Optional.ofNullable(packages).orElse(EMPTY_ARRAY)));
        return this;
    }

    @Override
    public Marker interfaces(Class<?>... interfaces) {
        this.interfaces.addAll(Arrays.asList(Optional.ofNullable(interfaces).orElse(EMPTY_CLASS)));
        return this;
    }

    @Override
    public Marker superType(Class<?> superType) {
        this.superType = superType;
        return this;
    }

    @Override
    public Marker name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Marker create(MethodDescribe methodDescribe) {
        methodDescribes.add(methodDescribe);
        return this;
    }

    @Override
    public Marker create(FieldDescribe fieldDescribe) {
        fieldDescribes.add(fieldDescribe);
        return this;
    }

    @Override
    public <T> T marker(Class<T> target) {
        ClassPool classPool = JavassistUtils.getClassPool();
        classPool.appendClassPath(new LoaderClassPath(target.getClassLoader()));
        for (String aPackage : packages) {
            classPool.importPackage(aPackage);
        }

        String className = Strings.isNullOrEmpty(name) ? "MarkerInstanceFactory$" + RandomUtils.randomString(10) : name;
        if (ClassUtils.isPresent(name)) {
            return ClassUtils.forObject(name);
        }
        try {
            T entity = createInstance(classPool, className, target.isInterface());
            if (null == entity) {
                return ProxyUtils.newProxy(target, ClassUtils.getDefaultClassLoader(), (obj, method, args, proxy) -> null);
            }

            if (target.isAssignableFrom(entity.getClass())) {
                return entity;
            }
            return ProxyUtils.newProxy(target, ClassUtils.getDefaultClassLoader(), new BridgingMethodIntercept<>(target, entity));

        } catch (Exception e) {
            Marker marker = Marker.of(target);
            return ProxyUtils.newProxy(target, (obj, method, args, proxy) -> {
                if (null == entity) {
                    return null;
                }
                return marker.createBench(MethodDescribe.builder().method(method).build()).execute(args);
            });
        }
    }

    @Override
    public Marker ofMarker() {
        ClassPool classPool = JavassistUtils.getClassPool();
        classPool.appendClassPath(new LoaderClassPath(null == target ? ClassUtils.getDefaultClassLoader() : target.getClassLoader()));
        for (String aPackage : packages) {
            classPool.importPackage(aPackage);
        }

        String className = Strings.isNullOrEmpty(name) ? "MarkerInstanceFactory$" + RandomUtils.randomString(10) : name;
        if (ClassUtils.isPresent(name)) {
            return Marker.of(ClassUtils.forObject(name));
        }
        try {
            return Marker.of(createInstance(classPool, className, null != target && target.isInterface()));
        } catch (Exception e) {
            return Marker.of(null);
        }
    }

    /**
     * 实例化
     *
     * @param classPool   类池
     * @param className   类名
     * @param isInterface 是否是接口
     * @param <T>         类型
     * @return 对象
     */
    protected <T> T createInstance(final ClassPool classPool, final String className, final boolean isInterface) throws Exception {
        CtClass ctClass = classPool.makeClass(className);
        for (Class<?> anInterface : interfaces) {
            ctClass.addInterface(JavassistUtils.toCtClass(anInterface, classPool));
        }

        ObjectUtils.ifNone(superType, it -> {
            ctClass.setSuperclass(JavassistUtils.toCtClass((Class<?>) it, classPool));
        });

        doAnalysisTypeAnnotation(ctClass, classPool);
        doAnalysisField(ctClass, classPool);
        doAnalysisMethod(ctClass, classPool, isInterface);
        Object entity = JavassistUtils.toEntity(ctClass, classPool);
        if (entity != null) {
            doAnalysisFieldValue(this.entity);
        }
        return (T) entity;
    }

    /**
     * 渲染字段
     *
     * @param entity 对象
     */
    protected void doAnalysisFieldValue(Object entity) {
        Map<String, Object> item = new HashMap<>(1 << 4);
        fieldDescribes.forEach(it -> {
            Object defaultValue = it.defaultValue();
            if (null == defaultValue) {
                return;
            }

            item.put(it.name(), defaultValue);
        });

        BeanUtils.copyProperties(item, entity);
    }

    /**
     * 方法
     *
     * @param ctClass     类型
     * @param classPool   类池
     * @param isInterface 是否是接口
     */
    protected void doAnalysisMethod(CtClass ctClass, ClassPool classPool, boolean isInterface) {
        methodDescribes.forEach(methodDescribe -> {
            String type = methodDescribe.returnType();
            if (Strings.isNullOrEmpty(type)) {
                type = String.class.getTypeName();
            }
            try {
                CtMethod ctMethod = CtNewMethod.make(methodDescribe.modifiers(),
                        classPool.get(type),
                        methodDescribe.name(),
                        JavassistUtils.toCtClass(methodDescribe.parameterTypes(), classPool),
                        JavassistUtils.toCtClass(methodDescribe.exceptionTypes(), classPool),
                        StringUtils.defaultString(methodDescribe.body(), "{return null;}"),
                        ctClass
                );
                renderMethodAnnotation(ctMethod, ctClass, methodDescribe.annotationTypes());
                ctClass.addMethod(ctMethod);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 渲染注解
     *
     * @param ctMethod        方法
     * @param ctClass         类型
     * @param annotationTypes 类型
     */
    void renderMethodAnnotation(CtMethod ctMethod, CtClass ctClass, AnnotationDescribe[] annotationTypes) {
        if (annotationTypes.length == 0) {
            return;
        }

        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        List<javassist.bytecode.annotation.Annotation> annotations = new LinkedList<>();

        for (AnnotationDescribe annotationDescribe : annotationTypes) {
            annotations.add(renderAnnotation(annotationDescribe.getName(), annotationDescribe.getAnnotationParameterDescribes(), constPool));
        }
        javassist.bytecode.MethodInfo methodInfo = ctMethod.getMethodInfo();
        annotationsAttribute.setAnnotations(annotations.toArray(new javassist.bytecode.annotation.Annotation[0]));
        methodInfo.addAttribute(annotationsAttribute);
    }


    /**
     * 字段
     *
     * @param ctClass   类型
     * @param classPool 类池
     */
    protected void doAnalysisField(CtClass ctClass, ClassPool classPool) {
        fieldDescribes.forEach(fieldDescribe -> {
            try {
                CtField ctField = new CtField(JavassistUtils.toCtClass(fieldDescribe.returnType(), classPool), fieldDescribe.name(), ctClass);
                doAnalysisFieldAnnotation(ctField, ctClass, fieldDescribe.annotationTypes());
                ctClass.addField(ctField);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 字段注解
     *
     * @param ctField         字段名称
     * @param ctClass         类型
     * @param annotationTypes 类型
     */
    protected void doAnalysisFieldAnnotation(CtField ctField, CtClass ctClass, AnnotationDescribe[] annotationTypes) {
        if (annotationTypes.length == 0) {
            return;
        }

        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        FieldInfo fieldInfo = ctField.getFieldInfo();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        List<javassist.bytecode.annotation.Annotation> annotations = new LinkedList<>();
        for (AnnotationDescribe annotationType : annotationTypes) {
            List<AnnotationParameterDescribe> annotationParameterDescribes = annotationType.getAnnotationParameterDescribes();
            annotations.add(renderAnnotation(annotationType.getName(), annotationParameterDescribes, constPool));
        }

        annotationsAttribute.setAnnotations(annotations.toArray(new javassist.bytecode.annotation.Annotation[0]));
        fieldInfo.addAttribute(annotationsAttribute);
    }

    /**
     * 类型注解
     *
     * @param ctClass   类型
     * @param classPool 类池
     */
    protected void doAnalysisTypeAnnotation(CtClass ctClass, final ClassPool classPool) {
        if (ArrayUtils.isEmpty(annotationDescribes)) {
            return;
        }

        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        List<javassist.bytecode.annotation.Annotation> annotations = new LinkedList<>();
        for (AnnotationDescribe annotationType : annotationDescribes) {
            List<AnnotationParameterDescribe> annotationParameterDescribes = annotationType.getAnnotationParameterDescribes();
            annotations.add(renderAnnotation(annotationType.getName(), annotationParameterDescribes, constPool));
        }

        annotationsAttribute.setAnnotations(annotations.toArray(new javassist.bytecode.annotation.Annotation[0]));
        classFile.addAttribute(annotationsAttribute);
    }


    /**
     * 渲染注解
     *
     * @param typeName                     类名
     * @param annotationParameterDescribes 注解值
     * @param constPool                    类池
     * @return 注解
     */
    protected javassist.bytecode.annotation.Annotation renderAnnotation(String typeName, List<AnnotationParameterDescribe> annotationParameterDescribes, ConstPool constPool) {
        Map<String, Object> params = new HashMap<>(annotationParameterDescribes.size());
        for (AnnotationParameterDescribe describe : annotationParameterDescribes) {
            params.put(describe.getName(), describe.getValue());
        }
        javassist.bytecode.annotation.Annotation annotation1 = new javassist.bytecode.annotation.Annotation(typeName, constPool);
        this.addAnnotationValue(annotation1, params, constPool);

        return annotation1;
    }

    /**
     * 注解赋值
     *
     * @param annotation      待赋值
     * @param annotationValue 添加的注解
     * @param constPool       对象池
     */
    protected void addAnnotationValue(javassist.bytecode.annotation.Annotation annotation, Map<String, Object> annotationValue, ConstPool constPool) {
        if (null == annotationValue) {
            return;
        }

        for (Map.Entry<String, Object> entry : annotationValue.entrySet()) {
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

}
