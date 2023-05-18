package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ValuePair;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.*;

import java.util.Arrays;

public class ElementValueInfo
{
    private char               tag;
    private ElementValueType   elementValueType;
    private ConstantValue      constantValue;
    private EnumConstant       enumConstant;
    private String             classname;
    private AnnotationInfo     annotationInfo;
    private int                num_values;
    private ElementValueInfo[] elementValueInfos;

    public void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        tag = (char) binaryData.readByte();
        elementValueType = resolveType(tag);
        if (isPrimitive(elementValueType) || elementValueType == ElementValueType.STRING)
        {
            int          const_value_index = binaryData.readShort();
            ConstantInfo constantInfo      = constantInfos[const_value_index - 1];
            if (constantInfo instanceof IntegerInfo)
            {
                constantValue = new ConstantValue(elementValueType, ((IntegerInfo) constantInfo).getValue());
            }
            else if (constantInfo instanceof FloatInfo)
            {
                constantValue = new ConstantValue(elementValueType, ((FloatInfo) constantInfo).getValue());
            }
            else if (constantInfo instanceof LongInfo)
            {
                constantValue = new ConstantValue(elementValueType, ((LongInfo) constantInfo).getValue());
            }
            else if (constantInfo instanceof DoubleInfo)
            {
                constantValue = new ConstantValue(elementValueType, ((DoubleInfo) constantInfo).getValue());
            }
            else if (elementValueType == ElementValueType.STRING)
            {
                constantValue = new ConstantValue(elementValueType, ((Utf8Info) constantInfo).getValue());
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if (elementValueType == ElementValueType.ENUM)
        {
            int    type_name_index  = binaryData.readShort();
            int    const_name_index = binaryData.readShort();
            String typeName         = ((Utf8Info) constantInfos[type_name_index - 1]).getValue();
            String enumName         = ((Utf8Info) constantInfos[const_name_index - 1]).getValue();
            enumConstant = new EnumConstant(typeName, enumName);
        }
        else if (elementValueType == ElementValueType.CLASS)
        {
            int class_info_index = binaryData.readShort();
            classname = ((Utf8Info) constantInfos[class_info_index - 1]).getValue();
        }
        else if (elementValueType == ElementValueType.ANNOTATION)
        {
            annotationInfo = new AnnotationInfo();
            annotationInfo.resolve(binaryData, constantInfos);
        }
        else if (elementValueType == ElementValueType.ARRAY)
        {
            num_values = binaryData.readShort();
            elementValueInfos = new ElementValueInfo[num_values];
            for (int i = 0; i < num_values; i++)
            {
                elementValueInfos[i] = new ElementValueInfo();
                elementValueInfos[i].resolve(binaryData, constantInfos);
            }
        }
    }

    public boolean isPrimitive(ElementValueType type)
    {
        //
//
//
//
//
//
//
//
        return type == ElementValueType.BYTE //
                || type == ElementValueType.CHAR//
                || type == ElementValueType.DOUBLE//
                || type == ElementValueType.FLOAT//
                || type == ElementValueType.INT//
                || type == ElementValueType.LONG//
                || type == ElementValueType.SHORT//
                || type == ElementValueType.BOOLEAN;
    }

    ElementValueType resolveType(char c)
    {
        switch (c)
        {
            case 'B':
                return ElementValueType.BYTE;
            case 'C':
                return ElementValueType.CHAR;
            case 'D':
                return ElementValueType.DOUBLE;
            case 'F':
                return ElementValueType.FLOAT;
            case 'I':
                return ElementValueType.INT;
            case 'J':
                return ElementValueType.LONG;
            case 'S':
                return ElementValueType.SHORT;
            case 'Z':
                return ElementValueType.BOOLEAN;
            case 's':
                return ElementValueType.STRING;
            case 'e':
                return ElementValueType.ENUM;
            case 'c':
                return ElementValueType.CLASS;
            case '@':
                return ElementValueType.ANNOTATION;
            case '[':
                return ElementValueType.ARRAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString()
    {
        return "ElementValueInfo{" + "tag=" + tag + ", elementValueType=" + elementValueType + ", constantValue=" + constantValue + ", enumConstant=" + enumConstant + ", classname='" + classname + '\'' + ", annotationInfo=" + annotationInfo + ", num_values=" + num_values + ", elementValueInfos=" + Arrays.toString(elementValueInfos) + '}';
    }

    public ElementValueType getElementValueType()
    {
        return elementValueType;
    }

    public ConstantValue getConstantValue()
    {
        return constantValue;
    }

    public EnumConstant getEnumConstant()
    {
        return enumConstant;
    }

    public String getClassname()
    {
        return classname;
    }

    public AnnotationInfo getAnnotationInfo()
    {
        return annotationInfo;
    }

    public int getNum_values()
    {
        return num_values;
    }

    public ElementValueInfo[] getElementValueInfos()
    {
        return elementValueInfos;
    }

    public ValuePair getValue(ClassLoader classLoader, MethodInfo methodInfo)
    {
        ValuePair valuePair = new ValuePair(methodInfo);
        valuePair.setElementValueType(elementValueType);
        switch (elementValueType)
        {
            case BYTE:
                byte b = (byte) constantValue.getIntValue();
                valuePair.setB(b);
                break;
            case CHAR:
                char c = (char) constantValue.getIntValue();
                valuePair.setC(c);
                break;
            case DOUBLE:
                double doubleValue = constantValue.getDoubleValue();
                valuePair.setD(doubleValue);
                break;
            case FLOAT:
                float floatValue = constantValue.getFloatValue();
                valuePair.setF(floatValue);
                break;
            case INT:
                int intValue = constantValue.getIntValue();
                valuePair.setI(intValue);
                break;
            case LONG:
                long longValue = constantValue.getLongValue();
                valuePair.setL(longValue);
                break;
            case SHORT:
                short shortValue = (short) constantValue.getIntValue();
                valuePair.setS(shortValue);
                break;
            case BOOLEAN:
                boolean booleanValue = constantValue.getIntValue() > 0;
                valuePair.setBooleanValue(booleanValue);
                break;
            case STRING:
                String stringValue = constantValue.getStringValue();
                valuePair.setStringValue(stringValue);
                break;
            case ENUM:
                String typeName = enumConstant.getTypeName();
                String enumName = enumConstant.getEnumName();
                valuePair.setEnumTypeName(typeName.replace('/', '.'));
                valuePair.setEnumValueName(enumName);
                break;
            case CLASS:
                valuePair.setClassName(classname.substring(1, classname.length() - 1).replace('/', '.'));
                break;
            case ANNOTATION:
                valuePair.setAnnotation(annotationInfo.getAnnotation(classLoader));
                break;
            case ARRAY:
                if (num_values != 0)
                {
                    valuePair.setComponentType(elementValueInfos[0].getElementValueType());
                    ValuePair[] array = new ValuePair[num_values];
                    for (int i = 0; i < array.length; i++)
                    {
                        array[i] = elementValueInfos[i].getValue(classLoader, methodInfo);
                    }
                    valuePair.setArray(array);
                    if (valuePair.getComponentType() == ElementValueType.ENUM)
                    {
                        valuePair.setComponentEnumTypeName(elementValueInfos[0].getEnumConstant().getTypeName().replace('/', '.'));
                    }
                    else if (valuePair.getComponentType() == ElementValueType.ANNOTATION)
                    {
                        valuePair.setComponentAnnotationType(elementValueInfos[0].getAnnotationInfo().getType().replace('/', '.'));
                    }
                }
                else
                {
                    String           methodInfoDescriptor = methodInfo.getDescriptor();
                    int              index                = methodInfoDescriptor.indexOf(")");
                    char             c1                   = methodInfoDescriptor.charAt(index + 2);
                    ElementValueType componentType        = null;
                    switch (c1)
                    {
                        case 'B':
                            componentType = ElementValueType.BYTE;
                            break;
                        case 'C':
                            componentType = ElementValueType.CHAR;
                            break;
                        case 'D':
                            componentType = ElementValueType.DOUBLE;
                            break;
                        case 'F':
                            componentType = ElementValueType.FLOAT;
                            break;
                        case 'I':
                            componentType = ElementValueType.INT;
                            break;
                        case 'J':
                            componentType = ElementValueType.LONG;
                            break;
                        case 'S':
                            componentType = ElementValueType.SHORT;
                            break;
                        case 'Z':
                            componentType = ElementValueType.BOOLEAN;
                            break;
                        case 'L':
                            String reference = methodInfoDescriptor.substring(index + 3, methodInfoDescriptor.length() - 1);
                            if (reference.equals("java/lang/String"))
                            {
                                componentType = ElementValueType.STRING;
                            }
                            else if (reference.equals("java/lang/Class"))
                            {
                                componentType = ElementValueType.CLASS;
                            }
                            else
                            {
                                byte[]    bytes     = BytecodeUtil.loadBytecode(classLoader, reference);
                                ClassFile classFile = new ClassFileParser(new BinaryData(bytes)).parse();
                                if (classFile.isAnnotation())
                                {
                                    componentType = ElementValueType.ANNOTATION;
                                    valuePair.setComponentAnnotationType(reference.replace('/', '.'));
                                }
                                else if (classFile.isEnum())
                                {
                                    componentType = ElementValueType.ENUM;
                                    valuePair.setComponentEnumTypeName(reference.replace('/', '.'));
                                }
                                else
                                {
                                    ReflectUtil.throwException(new IllegalArgumentException());
                                }
                            }
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    valuePair.setComponentType(componentType);
                    valuePair.setArray(new ValuePair[0]);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return valuePair;
    }
}
