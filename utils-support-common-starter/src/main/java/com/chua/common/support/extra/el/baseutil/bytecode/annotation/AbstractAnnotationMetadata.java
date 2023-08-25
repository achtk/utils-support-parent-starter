package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAnnotationMetadata implements AnnotationMetadata {
    protected String resourceName;
    protected Map<String, ValuePair> attributes;
    protected List<AnnotationMetadata> presentAnnotations;
    protected Annotation annotation;
    protected ClassLoader loader;
    protected Class<?> annotationType;

    public AbstractAnnotationMetadata(String resourceName, Map<String, ValuePair> attributes, ClassLoader loader) {
        this.resourceName = resourceName;
        this.attributes = attributes;
        this.loader = loader;
    }

    @Override
    public boolean shouldIgnore() {
        //排除掉三个JDK自带的注解，否则会这三个会无限循环
        return type().equals(DocumentedName) || type().equals(RetentionName) || type().equals(TargetName);
    }

    @Override
    public ValuePair getAttribyte(String name) {
        return attributes.get(name);
    }

    @Override
    public Class<?> annotationType() {
        if (annotationType == null) {
            try {
                annotationType = loader.loadClass(resourceName.replace('/', '.'));
            } catch (ClassNotFoundException e) {
                ReflectUtil.throwException(e);
                annotationType = null;
            }
        }
        return annotationType;
    }

    @Override
    public boolean isAnnotation(String name) {
        return name != null && name.equals(resourceName);
    }

    @Override
    public String type() {
        return resourceName;
    }

    @Override
    public Annotation annotation() {
        if (annotation == null) {
            final Map<String, Object> values = new HashMap<String, Object>();
            for (Map.Entry<String, ValuePair> entry : attributes.entrySet()) {
                switch (entry.getValue().getElementValueType()) {
                    case BOOLEAN:
                        values.put(entry.getKey(), entry.getValue().isBooleanValue());
                        break;
                    case DOUBLE:
                        values.put(entry.getKey(), entry.getValue().getD());
                        break;
                    case SHORT:
                        values.put(entry.getKey(), entry.getValue().getS());
                        break;
                    case FLOAT:
                        values.put(entry.getKey(), entry.getValue().getF());
                        break;
                    case LONG:
                        values.put(entry.getKey(), entry.getValue().getL());
                        break;
                    case CHAR:
                        values.put(entry.getKey(), entry.getValue().getC());
                        break;
                    case BYTE:
                        values.put(entry.getKey(), entry.getValue().getB());
                        break;
                    case INT:
                        values.put(entry.getKey(), entry.getValue().getI());
                        break;
                    case ANNOTATION:
                        values.put(entry.getKey(), entry.getValue().getAnnotation().annotation());
                        break;
                    case STRING:
                        values.put(entry.getKey(), entry.getValue().getStringValue());
                        break;
                    case CLASS:
                        try {
                            values.put(entry.getKey(), loader.loadClass(entry.getValue().getClassName()));
                        } catch (ClassNotFoundException e) {
                            ReflectUtil.throwException(e);
                        }
                        break;
                    case ENUM:
                        try {
                            Class<Enum> enumClass = (Class<Enum>) loader.loadClass(entry.getValue().getEnumTypeName());
                            Object enumInstance = Enum.valueOf(enumClass, entry.getValue().getEnumValueName());
                            values.put(entry.getKey(), enumInstance);
                        } catch (Exception e) {
                            ReflectUtil.throwException(e);
                        }
                        break;
                    case ARRAY:
                        ValuePair valuePair = entry.getValue();
                        switch (valuePair.getComponentType()) {
                            case INT: {
                                int[] array = new int[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getI();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case BYTE: {
                                byte[] array = new byte[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getB();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case LONG: {
                                long[] array = new long[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getL();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case FLOAT: {
                                float[] array = new float[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getF();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case SHORT: {
                                short[] array = new short[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getS();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case DOUBLE: {
                                double[] array = new double[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getD();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case BOOLEAN: {
                                boolean[] array = new boolean[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].isBooleanValue();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case CHAR: {
                                char[] array = new char[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getC();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case STRING: {
                                String[] array = new String[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    array[i] = valuePair.getArray()[i].getStringValue();
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case CLASS: {
                                Class[] array = new Class[valuePair.getArray().length];
                                for (int i = 0; i < valuePair.getArray().length; i++) {
                                    try {
                                        array[i] = loader.loadClass(valuePair.getArray()[i].getClassName());
                                    } catch (Exception e) {
                                        ReflectUtil.throwException(e);
                                    }
                                }
                                values.put(entry.getKey(), array);
                                break;
                            }
                            case ENUM: {
                                try {
                                    String enumTypeName = valuePair.getComponentEnumTypeName();
                                    Class<Enum> aClass = (Class<Enum>) loader.loadClass(enumTypeName);
                                    Object array = Array.newInstance(aClass, valuePair.getArray().length);
                                    for (int i = 0; i < valuePair.getArray().length; i++) {
                                        Object enumInstance = Enum.valueOf(aClass, valuePair.getArray()[i].getEnumValueName());
                                        Array.set(array, i, enumInstance);
                                    }
                                    values.put(entry.getKey(), array);
                                    break;
                                } catch (Exception e) {
                                    ReflectUtil.throwException(e);
                                }
                            }
                            case ANNOTATION:
                                String annotationType = entry.getValue().getComponentAnnotationType();
                                try {
                                    Class<?> aClass = loader.loadClass(annotationType);
                                    Object array = Array.newInstance(aClass, valuePair.getArray().length);
                                    for (int i = 0; i < valuePair.getArray().length; i++) {
                                        Array.set(array, i, entry.getValue().getArray()[i].getAnnotation().annotation());
                                    }
                                    values.put(entry.getKey(), array);
                                    break;
                                } catch (ClassNotFoundException e) {
                                    ReflectUtil.throwException(e);
                                }
                        }
                        break;
                }
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                annotation = (Annotation) Proxy.newProxyInstance(classLoader, new Class[]{classLoader.loadClass(resourceName.replace('/', '.'))}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return values.get(method.getName());
                    }
                });
            } catch (ClassNotFoundException e) {
                ReflectUtil.throwException(e);
            }
        }
        return annotation;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Map<String, ValuePair> getAttributes() {
        return attributes;
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
