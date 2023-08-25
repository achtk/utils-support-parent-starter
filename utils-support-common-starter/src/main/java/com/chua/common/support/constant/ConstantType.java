package com.chua.common.support.constant;

/**
 * 类型
 *
 * @author CH
 */
public enum ConstantType {
    /**
     * utf8
     */
    UTF_8,
    /**
     * int
     */
    INTEGER,
    /**
     * float
     */
    FLOAT,
    /**
     * long
     */
    LONG,
    /**
     * double
     */
    DOUBLE,
    /**
     * class
     */
    CLASS,
    /**
     * string
     */
    STRING,
    /**
     * field
     */
    FIELD_REF,
    /**
     * method
     */
    METHOD_REF,
    /**
     * interface
     */
    INTERFACE_METHOD_REF,
    /**
     * type
     */
    NAME_AND_TYPE,
    /**
     * mh
     */
    METHOD_HANDLE,
    /**
     * mt
     */
    METHOD_TYPE,
    /**
     * invoke
     */
    INVOKE_DYNAMIC,
    /**
     * UNKNOWN
     */
    UNKNOWN,
    /**
     * b
     */
    BYTE,
    /**
     * c
     */
    CHAR,
    /**
     * i
     */
    INT,
    /**
     * s
     */
    SHORT,
    /**
     * b
     */
    BOOLEAN,
    /**
     * CHARACTER
     */
    CHARACTER,
    /**
     * OTHER
     */
    OTHER,
    /**
     * enum
     */
    ENUM,
    /**
     * ANNOTATION
     */
    ANNOTATION,
    /**
     * ARRAY
     */
    ARRAY;

    public static ConstantType byteValue(int value) {
        switch (value) {
            case 1:
                return UTF_8;
            case 3:
                return INTEGER;
            case 4:
                return FLOAT;
            case 5:
                return LONG;
            case 6:
                return DOUBLE;
            case 7:
                return CLASS;
            case 8:
                return STRING;
            case 9:
                return FIELD_REF;
            case 10:
                return METHOD_REF;
            case 11:
                return INTERFACE_METHOD_REF;
            case 12:
                return NAME_AND_TYPE;
            case 15:
                return METHOD_HANDLE;
            case 16:
                return METHOD_TYPE;
            case 18:
                return INVOKE_DYNAMIC;
            default:
                throw new IllegalArgumentException(java.lang.String.valueOf(value));
        }
    }
}
