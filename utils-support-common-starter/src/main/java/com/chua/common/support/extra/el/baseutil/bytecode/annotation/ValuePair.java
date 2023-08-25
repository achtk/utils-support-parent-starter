package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import lombok.Data;

@Data
public class ValuePair {
    private ConstantType elementValueType;
    private char c;
    private byte b;
    private int i;
    private short s;
    private long l;
    private float f;
    private double d;
    private boolean booleanValue;
    private String stringValue;
    private String className;
    private String enumTypeName;
    private String enumValueName;
    private ValuePair[] array;
    private ConstantType componentType;
    /**
     * 格式为aa.bb.cc
     */
    private String componentEnumTypeName;
    /**
     * 格式为aa.bb.cc
     */
    private String componentAnnotationType;
    private AnnotationMetadata annotation;
    private MethodInfo methodInfo;

    public ValuePair(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

}
