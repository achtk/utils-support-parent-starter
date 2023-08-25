package com.chua.common.support.extra.el.baseutil.reflect;


import com.chua.common.support.constant.ConstantType;

import static com.chua.common.support.constant.ConstantType.*;

public final class ReflectUtil {
    /**
     * 类型装枚举
     *
     * @param type 类型
     * @return ConstantType
     */
    public static ConstantType ofPrimitive(Class<?> type) {
        switch (type.getName()) {
            case "int":
            case "java.lang.Integer": {
                return INTEGER;
            }
            case "boolean":
            case "java.lang.Boolean": {
                return BOOLEAN;
            }
            case "byte":
            case "java.lang.Byte": {
                return BYTE;
            }
            case "short":
            case "java.lang.Short": {
                return SHORT;
            }
            case "long":
            case "java.lang.Long": {
                return LONG;
            }
            case "char":
            case "java.lang.Character": {
                return CHAR;
            }
            case "float":
            case "java.lang.Float": {
                return FLOAT;
            }
            case "double":
            case "java.lang.Double": {
                return DOUBLE;
            }
            case "java.lang.String": {
                return STRING;
            }
            default: {
                return UNKNOWN;
            }
        }
    }

    public static Class<?> wrapPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException();
        }
        if (type == int.class) {
            return Integer.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == char.class) {
            return Character.class;
        } else {
            return null;
        }
    }

    public static void throwException(Throwable t) {
        if (t == null) {
            throw new NullPointerException("传入的参数为null");
        } else {
            throw new IllegalArgumentException(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwException0(Throwable t) throws E {
        throw (E) t;
    }
}
