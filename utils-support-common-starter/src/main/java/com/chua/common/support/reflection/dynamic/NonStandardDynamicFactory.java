package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.lang.proxy.MethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.JavassistUtils;
import com.chua.common.support.utils.StringUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.METHOD_GETTER;
import static com.chua.common.support.constant.CommonConstant.METHOD_SETTER;

/**
 * 非标准工厂
 *
 * @author CH
 * @since 2021-11-11
 */
public class NonStandardDynamicFactory implements DynamicFactory {
    private String type;
    private final List<FieldInfo> fieldInfos = new LinkedList<>();
    private final List<MethodInfo> methodInfos = new LinkedList<>();
    private final List<TypeInfo> typeInfos = new LinkedList<>();
    private List<AnnotationFactory> fieldAnnotationFactory = new LinkedList<>();
    private List<AnnotationFactory> methdAnnotationFactory = new LinkedList<>();
    private ClassPool classPool;
    private CtClass ctClass;
    private String superType;
    private List<String> interfaces = new LinkedList<>();
    private List<String> packages = new LinkedList<>();
    private List<String[]> construct = new LinkedList<>();

    public static DynamicFactory create() {
        return new NonStandardDynamicFactory();
    }

    @Override
    public DynamicFactory name(String name) {
        this.type = name;
        return this;
    }

    @Override
    public DynamicFactory field(String name, String type, Object value) {
        if (null == name) {
            return this;
        }
        if (null == type) {
            if (null == value) {
                type = Object.class.getTypeName();
            } else {
                type = value.getClass().getTypeName();
            }
        }
        this.fieldInfos.add(new FieldInfo(name, type, value));
        return this;
    }

    @Override
    public DynamicFactory fieldAnnotation(AnnotationFactory annotationFactory) {
        this.fieldAnnotationFactory.add(annotationFactory);
        return this;
    }

    @Override
    public DynamicFactory typeAnnotation(String name, Map<String, Object> values) {
        typeInfos.add(new TypeInfo(name, values));
        return this;
    }

    @Override
    public DynamicFactory method(String name, String returnType, String[] paramTypes, com.chua.common.support.reflection.dynamic.MethodFactory methodFactory) {
        this.methodInfos.add(new MethodInfo(name, returnType, paramTypes, methodFactory));
        return this;
    }

    @Override
    public DynamicFactory methodAnnotation(AnnotationFactory annotationFactory) {
        this.methdAnnotationFactory.add(annotationFactory);
        return this;
    }

    @Override
    public DynamicFactory construct(String... types) {
        construct.add(types);
        return this;
    }

    @Override
    public DynamicFactory superType(String superType) {
        this.superType = superType;
        return this;
    }

    @Override
    public DynamicFactory interfaces(String... types) {
        interfaces.addAll(Arrays.asList(types));
        return this;
    }

    @Override
    public DynamicFactory packages(String... packages) {
        this.packages.addAll(Arrays.asList(packages));
        return this;
    }

    @Override
    public <T> T toBean(Class<T> type, MethodIntercept methodIntercept) throws Exception {

        this.classPool = new ClassPool(true);
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        this.ctClass = classPool.makeClass(StringUtils.isNullOrEmpty(this.type) ? "random$" + System.currentTimeMillis() : this.type);
        //导入包
        doAnalysisPackage();
        //渲染字段
        doAnalysisField();
        //渲染父类
        doAnalysisSuperType();
        //渲染接口
        doAnalysisInterface();
        //渲染构造
        doAnalysisConstruct();
        //渲染类注解
        doAnalysisTypeAnnotation();
        //渲染方法
        doAnalysisMethod();
        //渲染对象
        Object object = getObject();
        if (null == object) {
            return null;
        }
        doAnalysisFieldValue(object);

        if (null == type || type.isAssignableFrom(object.getClass())) {
            return (T) object;
        }


        return (T) ProxyUtils.newProxy(type, null == methodIntercept ? (obj, method, args, proxy, p) -> com.chua.common.support.reflection.dynamic.MethodFactoryDefinition.of(object, method).invoke(args) : methodIntercept);
    }

    /**
     * 初始化值
     *
     * @param object 生成对象
     */
    private void doAnalysisFieldValue(Object object) {
        Class<?> aClass = object.getClass();
        for (FieldInfo fieldInfo : fieldInfos) {
            Object value = fieldInfo.getValue();
            if (null == value) {
                continue;
            }

            try {
                Field field = aClass.getDeclaredField(fieldInfo.name);
                field.setAccessible(true);
                field.set(object, value);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 导入包
     */
    private void doAnalysisPackage() {
        for (String aPackage : packages) {
            classPool.importPackage(aPackage);
        }
    }

    /**
     * 渲染类注解
     */
    private void doAnalysisTypeAnnotation() {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        List<Annotation> rs = new LinkedList<>();

        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (TypeInfo typeInfo : typeInfos) {
            for (AnnotationFactory annotationFactory : methdAnnotationFactory) {
                String column = annotationFactory.annotationName(typeInfo.getName());
                String annotationName = annotationFactory.annotationName(column);
                if (StringUtils.isNullOrEmpty(annotationName)) {
                    continue;
                }
                Annotation annotation1 = new Annotation(annotationName, constPool);
                this.addAnnotationValue(annotation1, annotationFactory.annotationValues(column), constPool);
                rs.add(annotation1);
            }
        }
        annotationsAttribute.setAnnotations(rs.toArray(new Annotation[0]));
        classFile.addAttribute(annotationsAttribute);
    }

    /**
     * 渲染构造
     */
    private void doAnalysisConstruct() throws Exception {
        for (String[] strings : construct) {
            CtClass[] paramsCtClass = new CtClass[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String string = strings[i];
                paramsCtClass[i] = classPool.get(string);
            }
            CtConstructor constructor = null;
            try {
                constructor = ctClass.getDeclaredConstructor(paramsCtClass);
            } catch (NotFoundException ignored) {
            }
            if (null != constructor) {
                ctClass.removeConstructor(constructor);
            }
            ctClass.addConstructor(constructor);
        }
    }

    /**
     * 渲染接口
     */
    private void doAnalysisInterface() throws Exception {
        for (String anInterface : interfaces) {
            ctClass.addInterface(classPool.get(anInterface));
        }
    }

    /**
     * 渲染父类
     */
    private void doAnalysisSuperType() throws Exception {
        if (StringUtils.isNullOrEmpty(superType)) {
            return;
        }
        ctClass.setSuperclass(classPool.get(superType));
    }

    /**
     * 渲染方法
     */
    private void doAnalysisMethod() throws Exception {

        for (MethodInfo info : methodInfos) {
            String type = Optional.ofNullable(info.getReturnType()).orElse(Void.class.getName());
            CtClass returnCtClass = classPool.get(type);
            if (null == returnCtClass) {
                continue;
            }
            CtClass[] paramsCtClass = new CtClass[null == info.getParamTypes() ? 0 : info.getParamTypes().length];
            String name = info.getName();
            for (int i = 0; i < info.getParamTypes().length; i++) {
                String paramType = info.getParamTypes()[i];
                paramsCtClass[i] = classPool.get(paramType);
            }
            CtMethod declaredMethod = null;
            try {
                declaredMethod = returnCtClass.getDeclaredMethod(name, paramsCtClass);
            } catch (NotFoundException ignored) {
            }
            if (null != declaredMethod) {
                returnCtClass.removeMethod(declaredMethod);
            }
            try {
                CtMethod make = new CtMethod(returnCtClass, name, paramsCtClass, ctClass);
                make.setBody(CommonConstant.SYMBOL_LEFT_BIG_PARENTHESES + info.getMethodFactory().body(name) + CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES);
                ctClass.addMethod(make);
                //渲染注解
                doAnalysisMethodAnnotation(make);
            } catch (CannotCompileException ignored) {
            }
        }

    }

    /**
     * 渲染字段
     */
    private void doAnalysisField() throws Exception {
        for (FieldInfo info : fieldInfos) {
            String type = info.getType();

            if (null == type) {
                continue;
            }

            CtClass ctClass1 = null;
            ctClass1 = classPool.get(type);
            if (null == ctClass1) {
                continue;
            }
            String name = info.getName();
            String getMethod = METHOD_GETTER + NamingCase.toFirstUpperCase(name);
            String setMethod = METHOD_SETTER + NamingCase.toFirstUpperCase(name);
            try {
                clearField(name, getMethod, setMethod);
                CtField ctField = new CtField(ctClass1, name, ctClass);
                ctClass.addField(ctField);
                ctClass.addMethod(CtNewMethod.getter(getMethod, ctField));
                ctClass.addMethod(CtNewMethod.setter(setMethod, ctField));
                doAnalysisFieldAnnotation(ctField);
            } catch (CannotCompileException ignored) {
            }
        }

        //渲染注解
    }

    /**
     * 清除字段
     *
     * @param name      字段名
     * @param getMethod get
     * @param setMethod set
     */
    private void clearField(String name, String getMethod, String setMethod) throws Exception {
        CtField oldField = null;
        try {
            oldField = ctClass.getDeclaredField(name);
        } catch (NotFoundException ignored) {
        }
        if (oldField != null) {
            ctClass.removeField(oldField);
        }
        CtMethod declaredMethod = null;
        try {
            declaredMethod = ctClass.getDeclaredMethod(getMethod);
        } catch (Exception ignored) {
        }
        if (declaredMethod != null) {
            ctClass.removeMethod(declaredMethod);
        }
        CtMethod ctMethod = null;
        if (null != oldField) {
            try {
                ctMethod = ctClass.getDeclaredMethod(setMethod, new CtClass[]{oldField.getType()});
            } catch (Exception ignored) {
            }
        }
        if (ctMethod != null) {
            ctClass.removeMethod(ctMethod);
        }
    }

    /**
     * 渲染对象
     *
     * @return 对象
     */
    public Object getObject() {
        //类加载器
        ClassLoader callerClassLoader = ClassUtils.getCallerClassLoader(this.getClass());
        //渲染对象
        Class<?> type = JavassistUtils.loader(ctClass, classPool, callerClassLoader);
        //结果
        Object forObject = ClassUtils.forObject(type);
        if (null != forObject) {
            try {
                forObject = ClassUtils.forObject(Thread.currentThread().getContextClassLoader().loadClass(forObject.getClass().getTypeName()));
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (null == forObject) {
            return null;
        }
        Map<String, Object> fieldValue = new HashMap<>(1 << 4);
        for (FieldInfo fieldInfo : fieldInfos) {
            Object value = fieldInfo.getValue();
            if (null == value) {
                continue;
            }
            fieldValue.put(fieldInfo.getName(), value);
        }
        //渲染字段
        BeanUtils.copyProperties(fieldValue, forObject);
        return forObject;
    }

    /**
     * 渲染字段注解
     *
     * @param ctField 字段
     */
    private void doAnalysisFieldAnnotation(CtField ctField) {
        if (null == fieldAnnotationFactory || null == ctField) {
            return;
        }
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        List<Annotation> rs = new LinkedList<>();

        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.FieldInfo fieldInfo = ctField.getFieldInfo();
        for (AnnotationFactory annotationFactory : fieldAnnotationFactory) {
            String column = annotationFactory.annotationName(ctField.getName());

            String annotationName = annotationFactory.annotationName(column);
            if (StringUtils.isNullOrEmpty(annotationName)) {
                return;
            }
            Annotation annotation1 = new Annotation(annotationName, constPool);
            this.addAnnotationValue(annotation1, annotationFactory.annotationValues(column), constPool);
            rs.add(annotation1);
        }
        annotationsAttribute.setAnnotations(rs.toArray(new Annotation[0]));
        fieldInfo.addAttribute(annotationsAttribute);
//        classFile.addAttribute(annotationsAttribute);
    }

    /**
     * 渲染字段注解
     *
     * @param ctMethod 字段
     */
    private void doAnalysisMethodAnnotation(CtMethod ctMethod) {
        if (null == methdAnnotationFactory || null == ctMethod) {
            return;
        }
        ClassFile classFile = ctClass.getClassFile();
        List<Annotation> rs = new LinkedList<>();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.MethodInfo methodInfo = ctMethod.getMethodInfo();
        for (AnnotationFactory annotationFactory : methdAnnotationFactory) {
            String column = annotationFactory.annotationName(ctMethod.getName());
            String annotationName = annotationFactory.annotationName(column);
            if (StringUtils.isNullOrEmpty(annotationName)) {
                return;
            }

            try {
                if (!annotationFactory.isMath(ctMethod.getName(), JavassistUtils.toTypeName(ctMethod.getParameterTypes()))) {
                    return;
                }
            } catch (NotFoundException ignored) {
            }
            Annotation annotation1 = new Annotation(annotationName, constPool);
            this.addAnnotationValue(annotation1, annotationFactory.annotationValues(column), constPool);
            rs.add(annotation1);
        }
        annotationsAttribute.setAnnotations(rs.toArray(new Annotation[0]));
        methodInfo.addAttribute(annotationsAttribute);
         classFile.addAttribute(annotationsAttribute);
    }

    /**
     * 注解赋值
     *
     * @param annotation      待赋值
     * @param annotationValue 添加的注解
     * @param constPool       对象池
     */
    private void addAnnotationValue(Annotation annotation, Map<String, Object> annotationValue, ConstPool constPool) {
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

    @Data
    @AllArgsConstructor
    final class MethodInfo {
        private String name;
        private String returnType;
        private String[] paramTypes;
        private com.chua.common.support.reflection.dynamic.MethodFactory methodFactory;
    }

    @Data
    @AllArgsConstructor
    final class FieldInfo {
        private String name;
        private String type;
        private Object value;
    }

    @Data
    @AllArgsConstructor
    final class TypeInfo {
        private String name;
        private Map<String, Object> value;
    }
}
