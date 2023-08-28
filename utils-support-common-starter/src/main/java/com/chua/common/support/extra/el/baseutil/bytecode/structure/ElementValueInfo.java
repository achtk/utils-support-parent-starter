package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ValuePair;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.*;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import java.util.Arrays;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH_CHAR;
import static com.chua.common.support.constant.ConstantType.*;
import static com.chua.common.support.constant.NameConstant.CLASS_NAME;
import static com.chua.common.support.constant.NameConstant.STRING_NAME;

public class ElementValueInfo {
    private char tag;
    private ConstantType elementValueType;
    private ConstantValue constantValue;
    private EnumConstant enumConstant;
    private String classname;
    private AnnotationInfo annotationInfo;
    private int numValues;
    private ElementValueInfo[] elementValueInfos;

    public void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        tag = (char) binaryData.readByte();
        elementValueType = resolveType(tag);
        if (isPrimitive(elementValueType) || elementValueType == STRING) {
            int constValueIndex = binaryData.readShort();
            AbstractConstantInfo constantInfo = constantInfos[constValueIndex - 1];
            if (constantInfo instanceof IntegerInfo) {
                constantValue = new ConstantValue(elementValueType, ((IntegerInfo) constantInfo).getValue());
            } else if (constantInfo instanceof FloatInfo) {
                constantValue = new ConstantValue(elementValueType, ((FloatInfo) constantInfo).getValue());
            } else if (constantInfo instanceof LongInfo) {
                constantValue = new ConstantValue(elementValueType, ((LongInfo) constantInfo).getValue());
            } else if (constantInfo instanceof DoubleInfo) {
                constantValue = new ConstantValue(elementValueType, ((DoubleInfo) constantInfo).getValue());
            } else if (elementValueType == STRING) {
                constantValue = new ConstantValue(elementValueType, ((Utf8Info) constantInfo).getValue());
            } else {
                throw new IllegalArgumentException();
            }
        } else if (elementValueType == ENUM) {
            int typeNameIndex = binaryData.readShort();
            int constNameIndex = binaryData.readShort();
            String typeName = ((Utf8Info) constantInfos[typeNameIndex - 1]).getValue();
            String enumName = ((Utf8Info) constantInfos[constNameIndex - 1]).getValue();
            enumConstant = new EnumConstant(typeName, enumName);
        } else if (elementValueType == CLASS) {
            int classInfoIndex = binaryData.readShort();
            classname = ((Utf8Info) constantInfos[classInfoIndex - 1]).getValue();
        } else if (elementValueType == ANNOTATION) {
            annotationInfo = new AnnotationInfo();
            annotationInfo.resolve(binaryData, constantInfos);
        } else if (elementValueType == ARRAY) {
            numValues = binaryData.readShort();
            elementValueInfos = new ElementValueInfo[numValues];
            for (int i = 0; i < numValues; i++) {
                elementValueInfos[i] = new ElementValueInfo();
                elementValueInfos[i].resolve(binaryData, constantInfos);
            }
        }
    }

    public boolean isPrimitive(ConstantType type) {
        //
//
//
//
//
//
//
//
        return type == BYTE //
                || type == CHAR//
                || type == DOUBLE//
                || type == FLOAT//
                || type == INT//
                || type == LONG//
                || type == SHORT//
                || type == BOOLEAN;
    }

    ConstantType resolveType(char c) {
        switch (c) {
            case 'B':
                return BYTE;
            case 'C':
                return CHAR;
            case 'D':
                return DOUBLE;
            case 'F':
                return FLOAT;
            case 'I':
                return INT;
            case 'J':
                return LONG;
            case 'S':
                return SHORT;
            case 'Z':
                return BOOLEAN;
            case 's':
                return STRING;
            case 'e':
                return ENUM;
            case 'c':
                return CLASS;
            case '@':
                return ANNOTATION;
            case '[':
                return ARRAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "ElementValueInfo{" + "tag=" + tag + ", elementValueType=" + elementValueType + ", constantValue=" + constantValue + ", enumConstant=" + enumConstant + ", classname='" + classname + '\'' + ", annotationInfo=" + annotationInfo + ", num_values=" + numValues + ", elementValueInfos=" + Arrays.toString(elementValueInfos) + '}';
    }

    public ConstantType getElementValueType() {
        return elementValueType;
    }

    public ConstantValue getConstantValue() {
        return constantValue;
    }

    public EnumConstant getEnumConstant() {
        return enumConstant;
    }

    public String getClassname() {
        return classname;
    }

    public AnnotationInfo getAnnotationInfo() {
        return annotationInfo;
    }

    public int getNumValues() {
        return numValues;
    }

    public ElementValueInfo[] getElementValueInfos() {
        return elementValueInfos;
    }

    public ValuePair getValue(ClassLoader classLoader, MethodInfo methodInfo) {
        ValuePair valuePair = new ValuePair(methodInfo);
        valuePair.setElementValueType(elementValueType);
        switch (elementValueType) {
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
                if (numValues != 0) {
                    valuePair.setComponentType(elementValueInfos[0].getElementValueType());
                    ValuePair[] array = new ValuePair[numValues];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = elementValueInfos[i].getValue(classLoader, methodInfo);
                    }
                    valuePair.setArray(array);
                    if (valuePair.getComponentType() == ENUM) {
                        valuePair.setComponentEnumTypeName(elementValueInfos[0].getEnumConstant().getTypeName().replace('/', '.'));
                    } else if (valuePair.getComponentType() == ANNOTATION) {
                        valuePair.setComponentAnnotationType(elementValueInfos[0].getAnnotationInfo().getType().replace('/', '.'));
                    }
                } else {
                    String methodInfoDescriptor = methodInfo.getDescriptor();
                    int index = methodInfoDescriptor.indexOf(")");
                    char c1 = methodInfoDescriptor.charAt(index + 2);
                    ConstantType componentType = null;
                    switch (c1) {
                        case 'B':
                            componentType = BYTE;
                            break;
                        case 'C':
                            componentType = CHAR;
                            break;
                        case 'D':
                            componentType = DOUBLE;
                            break;
                        case 'F':
                            componentType = FLOAT;
                            break;
                        case 'I':
                            componentType = INT;
                            break;
                        case 'J':
                            componentType = LONG;
                            break;
                        case 'S':
                            componentType = SHORT;
                            break;
                        case 'Z':
                            componentType = BOOLEAN;
                            break;
                        case 'L':
                            String reference = methodInfoDescriptor.substring(index + 3, methodInfoDescriptor.length() - 1);
                            if (STRING_NAME.equals(reference)) {
                                componentType = STRING;
                            } else if (CLASS_NAME.equals(reference)) {
                                componentType = CLASS;
                            } else {
                                byte[] bytes = BytecodeUtil.loadBytecode(classLoader, reference);
                                ClassFile classFile = new ClassFileParser(new BinaryData(bytes)).parse();
                                if (classFile.isAnnotation()) {
                                    componentType = ANNOTATION;
                                    valuePair.setComponentAnnotationType(reference.replace(SYMBOL_LEFT_SLASH_CHAR, SYMBOL_DOT_CHAR));
                                } else if (classFile.isEnum()) {
                                    componentType = ENUM;
                                    valuePair.setComponentEnumTypeName(reference.replace(SYMBOL_LEFT_SLASH_CHAR, SYMBOL_DOT_CHAR));
                                } else {
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
