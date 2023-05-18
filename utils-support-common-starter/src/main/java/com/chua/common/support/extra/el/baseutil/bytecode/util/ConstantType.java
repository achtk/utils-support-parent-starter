package com.chua.common.support.extra.el.baseutil.bytecode.util;

public enum ConstantType
{
    Utf8,//
    Integer,//
    Float,//
    Long,//
    Double,//
    Class,//
    String,//
    FieldRef,//
    MethodRef,//
    InterfaceMethodref,//
    NameAndType,//
    MethodHandle,//
    MethodType,//
    InvokeDynamic,//

    //
    ;

    public static ConstantType byteValue(int value)
    {
        switch (value)
        {
            case 1:
                return Utf8;
            case 3:
                return Integer;
            case 4:
                return Float;
            case 5:
                return Long;
            case 6:
                return Double;
            case 7:
                return Class;
            case 8:
                return String;
            case 9:
                return FieldRef;
            case 10:
                return MethodRef;
            case 11:
                return InterfaceMethodref;
            case 12:
                return NameAndType;
            case 15:
                return MethodHandle;
            case 16:
                return MethodType;
            case 18:
                return InvokeDynamic;
            default:
                throw new IllegalArgumentException(java.lang.String.valueOf(value));
        }
    }
}
