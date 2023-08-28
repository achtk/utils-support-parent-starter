package com.chua.common.support.extra.el.baseutil.bytecode;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.RuntimeVisibleAnnotationsAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.FieldInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.AccessFlags;
import javassist.bytecode.AttributeInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class ClassFile
{
    private int minorVersion;
    private int majorVersion;
    private int accessFlags;
    private String thisClassName;
    private String superClassName;
    private String[]                 interfaces;
    private FieldInfo[]              fieldInfos;
    private MethodInfo[]             methodInfos;
    private AbstractAttributeInfo[]          attributeInfos;
    private List<AnnotationMetadata> annotations;

    void setAttributeInfos(AbstractAttributeInfo[] attributeInfos)
    {
        this.attributeInfos = attributeInfos;
    }

    void setSuperClassName(String superClassName)
    {
        if (superClassName == null)
        {
            return;
        }
        if (superClassName.indexOf('/') != -1)
        {
            superClassName = superClassName.replace('/', '.');
        }
        this.superClassName = superClassName;
    }

    public List<AnnotationMetadata> getAnnotations(ClassLoader classLoader)
    {
        if (annotations != null)
        {
            return annotations;
        }
        RuntimeVisibleAnnotationsAttriInfo runtimeVisibleAnnotationsAttriInfo = null;
        for (AbstractAttributeInfo attributeInfo : attributeInfos)
        {
            if (attributeInfo instanceof RuntimeVisibleAnnotationsAttriInfo)
            {
                runtimeVisibleAnnotationsAttriInfo = (RuntimeVisibleAnnotationsAttriInfo) attributeInfo;
                break;
            }
        }
        if (runtimeVisibleAnnotationsAttriInfo == null || runtimeVisibleAnnotationsAttriInfo.getAnnotations().length == 0)
        {
            annotations = Collections.emptyList();
            return annotations;
        }
        annotations = new ArrayList<AnnotationMetadata>();
        for (AnnotationInfo info : runtimeVisibleAnnotationsAttriInfo.getAnnotations())
        {
            annotations.add(info.getAnnotation(classLoader));
        }
        return annotations;
    }

    public MethodInfo[] getMethodInfos()
    {
        return methodInfos;
    }

    void setMethodInfos(MethodInfo[] methodInfos)
    {
        this.methodInfos = methodInfos;
    }

    public boolean isInterface()
    {
        return (accessFlags & AccessFlags.ACC_INTERFACE) != 0;
    }

    public boolean isAnnotation()
    {
        return (accessFlags & AccessFlags.ACC_ANNOTATION) != 0;
    }

    public boolean isAbstract()
    {
        return (accessFlags & AccessFlags.ACC_ABSTRACT) != 0;
    }

    public boolean isEnum()
    {
        return (accessFlags & AccessFlags.ACC_ENUM) != 0;
    }

    public String[] getInterfaces()
    {
        return interfaces;
    }

    void setInterfaces(String[] interfaces)
    {
        this.interfaces = new String[interfaces.length];
        for (int i = 0; i < this.interfaces.length; i++)
        {
            String value = interfaces[i];
            if (value.indexOf('/') != -1)
            {
                value = value.replace('/', '.');
            }
            this.interfaces[i] = value;
        }
    }

    public String getSuperClassName()
    {
        return superClassName;
    }

    public String getThisClassName()
    {
        return thisClassName;
    }

    void setThisClassName(String thisClassName)
    {
        if (thisClassName.indexOf('/') != -1)
        {
            thisClassName = thisClassName.replace('/', '.');
        }
        this.thisClassName = thisClassName;
    }

    public boolean hasInterface(Class<?> ckass)
    {
        String name = ckass.getName();
        for (String each : interfaces)
        {
            if (each.equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isSuperClass(Class<?> ckass)
    {
        return ckass.getName().equals(superClassName);
    }

    @Override
    public String toString()
    {
        return "ClassFile{" + "minor_version=" + minorVersion + ", major_version=" + majorVersion + ", access_flags=" + accessFlags + ", this_class_name='" + thisClassName + '\'' + ", super_class_name='" + superClassName + '\'' + ", interfaces=" + Arrays.toString(interfaces) + ", fieldInfos=" + Arrays.toString(fieldInfos) + ", methodInfos=" + Arrays.toString(methodInfos) + ", attributeInfos=" + Arrays.toString(attributeInfos) + ", annotations=" + annotations + '}';
    }
}
