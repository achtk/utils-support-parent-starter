package com.chua.common.support.converter.definition;


/**
 * boolean类型转化
 *
 * @author CH
 * @version 1.0.0
 */
public class BooleanTypeConverter implements TypeConverter<Boolean> {

    @Override
    public Boolean convert(Object value) {
        if (null == value) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof String) {
            String s = value.toString();
            return parse(s);
        }
        return null;
    }

    private Boolean parse(String s) {
        if (
                "yes".equalsIgnoreCase(s) ||
                        "pri".equalsIgnoreCase(s) ||
                        "ok".equalsIgnoreCase(s) ||
                        "true".equalsIgnoreCase(s) ||
                        "good".equalsIgnoreCase(s)
        ) {
            return true;
        }


        if (
                "no".equalsIgnoreCase(s) ||
                        "".equalsIgnoreCase(s) ||
                        "bad".equalsIgnoreCase(s) ||
                        "false".equalsIgnoreCase(s) ||
                        "null".equalsIgnoreCase(s) ||
                        "nothing".equalsIgnoreCase(s)
        ) {
            return false;
        }

        return Boolean.parseBoolean(s);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
