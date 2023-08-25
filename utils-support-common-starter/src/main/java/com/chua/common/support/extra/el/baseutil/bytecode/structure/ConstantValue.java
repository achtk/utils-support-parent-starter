package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.constant.ConstantType;

public class ConstantValue {
    ConstantType type;
    int intValue;
    float floatValue;
    long longValue;
    double doubleValue;
    String stringValue;

    public ConstantValue(ConstantType type, String stringValue) {
        this.type = type;
        this.stringValue = stringValue;
    }

    public ConstantValue(ConstantType type, int intValue) {
        this.type = type;
        this.intValue = intValue;
    }

    public ConstantValue(ConstantType type, float floatValue) {
        this.type = type;
        this.floatValue = floatValue;
    }

    public ConstantValue(ConstantType type, long longValue) {
        this.type = type;
        this.longValue = longValue;
    }

    public ConstantValue(ConstantType type, double doubleValue) {
        this.type = type;
        this.doubleValue = doubleValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return "ConstantValue{" + "type=" + type + ", intValue=" + intValue + ", floatValue=" + floatValue + ", longValue=" + longValue + ", doubleValue=" + doubleValue + ", stringValue='" + stringValue + '\'' + '}';
    }
}
