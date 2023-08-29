package com.chua.common.support.extra.el.baseutil.bytecode.util;

import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ClassNotExistAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.FieldInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.CodeAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.LocalVariableTableAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.RuntimeVisibleAnnotationsAttriInfo;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;
import com.chua.common.support.utils.IoUtils;
import javassist.bytecode.AttributeInfo;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * 基础类
 * @author CH
 */
public class BytecodeUtil
{
    /**
     * 通过classloader搜索类名对应的.class文件，返回读取的字节码。如果字节码不存在，则返回null
     *
     * @param loader
     * @param name   格式为aa/bb/cc这种
     * @return
     */
    public static byte[] loadBytecode(ClassLoader loader, String name)
    {
        try
        {
            loader = loader == null ? Thread.currentThread().getContextClassLoader() : loader;
            InputStream resourceAsStream = loader.getResourceAsStream(name + ".class");
            if (resourceAsStream == null)
            {
                return null;
            }
            return IoUtils.toByteArray(resourceAsStream);
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    /**
     * 根据资源名称搜索对应的类文件并且解析二进制数据，如果无法搜索到，则返回null
     *
     * @param classLoader
     * @param resourceName 格式为aa/bb/cc
     * @return
     */
    public static ClassFile loadClassFile(ClassLoader classLoader, String resourceName)
    {
        InputStream resourceAsStream = classLoader.getResourceAsStream(resourceName + ".class");
        if (resourceAsStream == null)
        {
            return null;
        }
        try
        {
            byte[] content = new byte[resourceAsStream.available()];
            resourceAsStream.read(content);
            resourceAsStream.close();
            return new ClassFileParser(content).parse();
        }
        catch (Throwable e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    public static ClassFile loadClassFile(String resourceName)
    {
        return loadClassFile(Thread.currentThread().getContextClassLoader(), resourceName);
    }

    /**
     * 获取构造方法的入参名称，以数组的形式返回。获取方法入参名称依赖于编译器是否编译了入参名称到字节码中，因此可能存在获取失败的情况。如果获取失败，则返回null。
     *
     * @return
     */
    public static String[] parseConstructorParamNames(Constructor constructor)
    {
        Class     declaringClass = constructor.getDeclaringClass();
        ClassFile classFile      = loadClassFile(declaringClass.getClassLoader(), declaringClass.getName().replace('.', '/'));
        String    descriptor     = getConstructDescriptor(constructor);
        return parseParamNames(classFile, "<init>", descriptor, constructor.getParameterTypes().length, false);
    }

    private static String[] parseParamNames(ClassFile classFile, String methodName, String descriptor, int numOfName, boolean isStatic)
    {
        for (MethodInfo methodInfo : classFile.getMethodInfos())
        {
            if (methodInfo.getName().equals(methodName))
            {
                if (methodInfo.getDescriptor().equals(descriptor))
                {
                    for (AbstractAttributeInfo attributeInfo : methodInfo.getAttributeInfos())
                    {
                        if (attributeInfo instanceof CodeAttriInfo)
                        {
                            for (AbstractAttributeInfo info : ((CodeAttriInfo) attributeInfo).getAttributeInfos())
                            {
                                if (info instanceof LocalVariableTableAttriInfo)
                                {
                                    LocalVariableTableAttriInfo                           localVariableTableAttriInfo = (LocalVariableTableAttriInfo) info;
                                    LocalVariableTableAttriInfo.LocalVariableTableEntry[] entries                     = localVariableTableAttriInfo.getEntries();
                                    String[]                                              names                       = new String[numOfName];
                                    if (isStatic)
                                    {
                                        for (int i = 0; i < names.length; i++)
                                        {
                                            names[i] = entries[i].getName();
                                        }
                                    }
                                    else
                                    {
                                        for (int i = 0; i < names.length; i++)
                                        {
                                            names[i] = entries[i + 1].getName();
                                        }
                                    }
                                    return names;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取方法的入参名称，以数组的形式返回。获取方法入参名称依赖于编译器是否编译了入参名称到字节码中，因此可能存在获取失败的情况。如果获取失败，则返回null。
     *
     * @param method
     * @return
     */
    public static String[] parseMethodParamNames(Method method)
    {
        ClassFile classFile  = getMethodDeclaringClassFile(method);
        String    descriptor = getMethodDescriptor(method);
        return parseParamNames(classFile, method.getName(), descriptor, method.getParameterTypes().length, Modifier.isStatic(method.getModifiers()));
    }

    private static ClassFile getMethodDeclaringClassFile(Method method)
    {
        String name  = method.getDeclaringClass().getName().replace('.', '/');
        byte[] bytes = loadBytecode(method.getDeclaringClass().getClassLoader(), name);
        return new ClassFileParser(new BinaryData(bytes)).parse();
    }

    private static ClassFile getFieldDeclaringClassFile(Field field)
    {
        String name  = field.getDeclaringClass().getName().replace('.', '/');
        byte[] bytes = loadBytecode(field.getDeclaringClass().getClassLoader(), name);
        return new ClassFileParser(new BinaryData(bytes)).parse();
    }

    private static String getName(Class<?> parameterType)
    {
        if (parameterType.isPrimitive())
        {
            if (parameterType == int.class)
            {
                return "I";
            }
            else if (parameterType == short.class)
            {
                return "S";
            }
            else if (parameterType == long.class)
            {
                return "J";
            }
            else if (parameterType == float.class)
            {
                return "F";
            }
            else if (parameterType == double.class)
            {
                return "D";
            }
            else if (parameterType == char.class)
            {
                return "C";
            }
            else if (parameterType == byte.class)
            {
                return "B";
            }
            else if (parameterType == boolean.class)
            {
                return "Z";
            }
            else if (parameterType == void.class)
            {
                return "V";
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if (parameterType.isArray())
        {
            return "[" + getName(parameterType.getComponentType());
        }
        else
        {
            return "L" + parameterType.getName() + ";";
        }
    }

    /**
     * 找到在类上的所有有效注解
     *
     * @param name        资源名称，格式为aa/bb/cc
     * @param classLoader
     * @return
     */
    public static List<AnnotationMetadata> findAnnotationsOnClass(String name, ClassLoader classLoader)
    {
        name = name.replace('.', '/');
        byte[]                   bytecode  = loadBytecode(classLoader, name);
        ClassFile                classFile = new ClassFileParser(new BinaryData(bytecode)).parse();
        List<AnnotationMetadata> list      = new LinkedList<AnnotationMetadata>();
        for (AnnotationMetadata annotation : classFile.getAnnotations(classLoader))
        {
            if (annotation.shouldIgnore())
            {
                continue;
            }
            if (annotation instanceof ClassNotExistAnnotationMetadata == false)
            {
                list.add(annotation);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static List<AnnotationMetadata> findAnnotationsOnField(Field field, ClassLoader classLoader)
    {
        ClassFile classFile = getFieldDeclaringClassFile(field);
        String    fieldName = field.getName();
        for (FieldInfo fieldInfo : classFile.getFieldInfos())
        {
            if (fieldInfo.getName().equals(fieldName))
            {
                for (AbstractAttributeInfo attributeInfo : fieldInfo.getAttributeInfos())
                {
                    if (attributeInfo instanceof RuntimeVisibleAnnotationsAttriInfo)
                    {
                        return getAnnotationMetadata(classLoader, (RuntimeVisibleAnnotationsAttriInfo) attributeInfo);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * 找到在方法上所有有效注解
     *
     * @param method
     * @param loader
     * @return
     */
    public static List<AnnotationMetadata> findAnnotationsOnMethod(Method method, ClassLoader loader)
    {
        ClassFile classFile = getMethodDeclaringClassFile(method);
        return findAnnotationsOnMethod(method, loader, classFile);
    }

    private static List<AnnotationMetadata> findAnnotationsOnMethod(Method method, ClassLoader loader, ClassFile classFile)
    {
        String descriptor = getMethodDescriptor(method);
        String methodName = method.getName();
        for (MethodInfo methodInfo : classFile.getMethodInfos())
        {
            if (methodInfo.getName().equals(methodName))
            {
                if (methodInfo.getDescriptor().equals(descriptor))
                {
                    for (AbstractAttributeInfo attributeInfo : methodInfo.getAttributeInfos())
                    {
                        if (attributeInfo instanceof RuntimeVisibleAnnotationsAttriInfo)
                        {
                            return getAnnotationMetadata(loader, (RuntimeVisibleAnnotationsAttriInfo) attributeInfo);
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<AnnotationMetadata> getAnnotationMetadata(ClassLoader loader, RuntimeVisibleAnnotationsAttriInfo attributeInfo)
    {
        List<AnnotationMetadata>           list                               = new LinkedList<AnnotationMetadata>();
        RuntimeVisibleAnnotationsAttriInfo runtimeVisibleAnnotationsAttriInfo = attributeInfo;
        for (AnnotationInfo annotation : runtimeVisibleAnnotationsAttriInfo.getAnnotations())
        {
            AnnotationMetadata annotationMetadata = annotation.getAnnotation(loader);
            //排除掉三个JDK自带的注解，否则会这三个会无限循环
            if (annotationMetadata.shouldIgnore())
            {
                continue;
            }
            if (annotationMetadata instanceof ClassNotExistAnnotationMetadata == false)
            {
                list.add(annotationMetadata);
            }
        }
        return Collections.unmodifiableList(list);
    }

    private static String getMethodDescriptor(Method method)
    {
        StringBuilder cache = new StringBuilder();
        cache.append('(');
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes)
        {
            cache.append(getName(parameterType));
        }
        cache.append(')').append(getName(method.getReturnType()));
        return cache.toString().replace('.', '/');
    }

    private static String getConstructDescriptor(Constructor constructor)
    {
        StringBuilder cache = new StringBuilder();
        cache.append('(');
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (Class<?> parameterType : parameterTypes)
        {
            cache.append(getName(parameterType));
        }
        cache.append(")V");
        return cache.toString().replace('.', '/');
    }
}