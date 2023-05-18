package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.ElementValueType;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;

public class ValuePair
{
    private ElementValueType   elementValueType;
    private char               c;
    private byte               b;
    private int                i;
    private short              s;
    private long               l;
    private float              f;
    private double             d;
    private boolean            booleanValue;
    private String             stringValue;
    private String             className;
    private String             enumTypeName;
    private String             enumValueName;
    private ValuePair[]        array;
    private ElementValueType   componentType;
    /**
     * 格式为aa.bb.cc
     */
    private String             componentEnumTypeName;
    /**
     * 格式为aa.bb.cc
     */
    private String             componentAnnotationType;
    private AnnotationMetadata annotation;
    private MethodInfo         methodInfo;

    public ValuePair(MethodInfo methodInfo)
    {
        this.methodInfo = methodInfo;
    }

    public String getComponentAnnotationType()
    {
        return componentAnnotationType;
    }

    public void setComponentAnnotationType(String componentAnnotationType)
    {
        this.componentAnnotationType = componentAnnotationType;
    }

    public String getComponentEnumTypeName()
    {
        return componentEnumTypeName;
    }

    public void setComponentEnumTypeName(String componentEnumTypeName)
    {
        this.componentEnumTypeName = componentEnumTypeName;
    }

    public ElementValueType getComponentType()
    {
        return componentType;
    }

    public void setComponentType(ElementValueType componentType)
    {
        this.componentType = componentType;
    }

    public AnnotationMetadata getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(AnnotationMetadata annotation)
    {
        this.annotation = annotation;
    }

    public char getC()
    {
        return c;
    }

    public void setC(char c)
    {
        this.c = c;
    }

    public byte getB()
    {
        return b;
    }

    public void setB(byte b)
    {
        this.b = b;
    }

    public int getI()
    {
        return i;
    }

    public void setI(int i)
    {
        this.i = i;
    }

    public short getS()
    {
        return s;
    }

    public void setS(short s)
    {
        this.s = s;
    }

    public long getL()
    {
        return l;
    }

    public void setL(long l)
    {
        this.l = l;
    }

    public float getF()
    {
        return f;
    }

    public void setF(float f)
    {
        this.f = f;
    }

    public double getD()
    {
        return d;
    }

    public void setD(double d)
    {
        this.d = d;
    }

    public boolean booleanValue()
    {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue)
    {
        this.booleanValue = booleanValue;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getEnumTypeName()
    {
        return enumTypeName;
    }

    public void setEnumTypeName(String enumTypeName)
    {
        this.enumTypeName = enumTypeName;
    }

    public String getEnumValueName()
    {
        return enumValueName;
    }

    public void setEnumValueName(String enumValueName)
    {
        this.enumValueName = enumValueName;
    }

    public ValuePair[] getArray()
    {
        return array;
    }

    public void setArray(ValuePair[] array)
    {
        this.array = array;
    }

    public ElementValueType getElementValueType()
    {
        return elementValueType;
    }

    public void setElementValueType(ElementValueType elementValueType)
    {
        this.elementValueType = elementValueType;
    }
}
