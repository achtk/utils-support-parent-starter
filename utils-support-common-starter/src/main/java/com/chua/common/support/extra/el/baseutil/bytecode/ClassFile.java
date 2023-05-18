package com.chua.common.support.extra.el.baseutil.bytecode;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.RuntimeVisibleAnnotationsAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.FieldInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.AccessFlags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClassFile
{
    private int                      minor_version;
    private int                      major_version;
    private int                      access_flags;
    private String                   this_class_name;
    private String                   super_class_name;
    private String[]                 interfaces;
    private FieldInfo[]              fieldInfos;
    private MethodInfo[]             methodInfos;
    private AttributeInfo[]          attributeInfos;
    private List<AnnotationMetadata> annotations;

    void setAttributeInfos(AttributeInfo[] attributeInfos)
    {
        this.attributeInfos = attributeInfos;
    }

    void setSuper_class_name(String super_class_name)
    {
        if (super_class_name == null)
        {
            return;
        }
        if (super_class_name.indexOf('/') != -1)
        {
            super_class_name = super_class_name.replace('/', '.');
        }
        this.super_class_name = super_class_name;
    }

    public int getMinor_version()
    {
        return minor_version;
    }

    void setMinor_version(int minor_version)
    {
        this.minor_version = minor_version;
    }

    public int getMajor_version()
    {
        return major_version;
    }

    void setMajor_version(int major_version)
    {
        this.major_version = major_version;
    }

    public void setAccess_flags(int access_flags)
    {
        this.access_flags = access_flags;
    }

    public FieldInfo[] getFieldInfos()
    {
        return fieldInfos;
    }

    void setFieldInfos(FieldInfo[] fieldInfos)
    {
        this.fieldInfos = fieldInfos;
    }

    public List<AnnotationMetadata> getAnnotations(ClassLoader classLoader)
    {
        if (annotations != null)
        {
            return annotations;
        }
        RuntimeVisibleAnnotationsAttriInfo runtimeVisibleAnnotationsAttriInfo = null;
        for (AttributeInfo attributeInfo : attributeInfos)
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
        return (access_flags & AccessFlags.ACC_INTERFACE) != 0;
    }

    public boolean isAnnotation()
    {
        return (access_flags & AccessFlags.ACC_ANNOTATION) != 0;
    }

    public boolean isAbstract()
    {
        return (access_flags & AccessFlags.ACC_ABSTRACT) != 0;
    }

    public boolean isEnum()
    {
        return (access_flags & AccessFlags.ACC_ENUM) != 0;
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
        return super_class_name;
    }

    public String getThis_class_name()
    {
        return this_class_name;
    }

    void setThis_class_name(String this_class_name)
    {
        if (this_class_name.indexOf('/') != -1)
        {
            this_class_name = this_class_name.replace('/', '.');
        }
        this.this_class_name = this_class_name;
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
        return ckass.getName().equals(super_class_name);
    }

    @Override
    public String toString()
    {
        return "ClassFile{" + "minor_version=" + minor_version + ", major_version=" + major_version + ", access_flags=" + access_flags + ", this_class_name='" + this_class_name + '\'' + ", super_class_name='" + super_class_name + '\'' + ", interfaces=" + Arrays.toString(interfaces) + ", fieldInfos=" + Arrays.toString(fieldInfos) + ", methodInfos=" + Arrays.toString(methodInfos) + ", attributeInfos=" + Arrays.toString(attributeInfos) + ", annotations=" + annotations + '}';
    }
}
