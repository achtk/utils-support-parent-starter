package com.chua.common.support.extra.el.baseutil.bytecode.structure;

public class ConstantValue
{
    ElementValueType type;
    int              intValue;
    float            floatValue;
    long             longValue;
    double           doubleValue;
    String           stringValue;

    public ConstantValue(ElementValueType type, String stringValue)
    {
        this.type = type;
        this.stringValue = stringValue;
    }

    public ConstantValue(ElementValueType type, int intValue)
    {
        this.type = type;
        this.intValue = intValue;
    }

    public ConstantValue(ElementValueType type, float floatValue)
    {
        this.type = type;
        this.floatValue = floatValue;
    }

    public ConstantValue(ElementValueType type, long longValue)
    {
        this.type = type;
        this.longValue = longValue;
    }

    public ConstantValue(ElementValueType type, double doubleValue)
    {
        this.type = type;
        this.doubleValue = doubleValue;
    }

    public int getIntValue()
    {
        return intValue;
    }

    public float getFloatValue()
    {
        return floatValue;
    }

    public long getLongValue()
    {
        return longValue;
    }

    public double getDoubleValue()
    {
        return doubleValue;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    @Override
    public String toString()
    {
        return "ConstantValue{" + "type=" + type + ", intValue=" + intValue + ", floatValue=" + floatValue + ", longValue=" + longValue + ", doubleValue=" + doubleValue + ", stringValue='" + stringValue + '\'' + '}';
    }
}
