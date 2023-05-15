package com.chua.common.support.converter.definition;


import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Double数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class DoubleArrayTypeConverter implements TypeConverter<Double[]> {


    @Override
    public Double[] convert(Object value) {
        if (null == value) {
            return new Double[0];
        }

        Double[] valueArray = transToArray(value, Double.class);
        if (valueArray.length != 0) {
            return valueArray;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return transToArray(stringValue.split(SYMBOL_COMMA), Double.class);
        }

        return new Double[0];
    }

    @Override
    public Class<Double[]> getType() {
        return Double[].class;
    }

}
