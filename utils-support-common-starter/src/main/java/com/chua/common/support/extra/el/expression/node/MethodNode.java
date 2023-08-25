package com.chua.common.support.extra.el.expression.node;

import com.chua.common.support.constant.ConstantType;

import static com.chua.common.support.constant.ConstantType.*;

public interface MethodNode extends CalculateNode {
    void setArgsNodes(CalculateNode[] argsNodes);

    class MethodNodeUtil {
        public static void convertArgs(Object[] args, ConstantType[] convertTypes) {
            for (int i = 0; i < args.length; i++) {
                Object argeValue = args[i];
                switch (convertTypes[i]) {
                    case INT:
                        if (!(argeValue instanceof Integer)) {
                            args[i] = ((Number) argeValue).intValue();
                        }
                        break;
                    case LONG:
                        if (!(argeValue instanceof Long)) {
                            args[i] = ((Number) argeValue).longValue();
                        }
                        break;
                    case SHORT:
                        if (!(argeValue instanceof Short)) {
                            args[i] = ((Number) argeValue).shortValue();
                        }
                        break;
                    case FLOAT:
                        if (!(argeValue instanceof Float)) {
                            args[i] = ((Number) argeValue).floatValue();
                        }
                        break;
                    case DOUBLE:
                        if (!(argeValue instanceof Double)) {
                            args[i] = ((Number) argeValue).doubleValue();
                        }
                        break;
                    case BYTE:
                        if (!(argeValue instanceof Byte)) {
                            args[i] = ((Number) argeValue).byteValue();
                        }
                        break;
                    case CHARACTER:
                    case BOOLEAN:
                    case OTHER:
                        // 以上三种不用转化
                        break;
                    default:
                        break;
                }
            }
        }

        public static ConstantType[] buildConvertTypes(Class<?>[] parameterTypes) {
            ConstantType[] convertTypes = new ConstantType[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == int.class || parameterTypes[i] == Integer.class) {
                    convertTypes[i] = INT;
                } else if (parameterTypes[i] == short.class || parameterTypes[i] == Short.class) {
                    convertTypes[i] = SHORT;
                } else if (parameterTypes[i] == long.class || parameterTypes[i] == Long.class) {
                    convertTypes[i] = LONG;
                } else if (parameterTypes[i] == float.class || parameterTypes[i] == Float.class) {
                    convertTypes[i] = FLOAT;
                } else if (parameterTypes[i] == double.class || parameterTypes[i] == Double.class) {
                    convertTypes[i] = DOUBLE;
                } else if (parameterTypes[i] == byte.class || parameterTypes[i] == Byte.class) {
                    convertTypes[i] = BYTE;
                } else if (parameterTypes[i] == boolean.class || parameterTypes[i] == Boolean.class) {
                    convertTypes[i] = BOOLEAN;
                } else {
                    convertTypes[i] = OTHER;
                }
            }
            return convertTypes;
        }

        public static boolean isWrapType(Class<?> primitiveType, Class<?> arge) {
            if (primitiveType == int.class) {
                return arge == Integer.class || arge == Long.class;
            } else if (primitiveType == short.class) {
                return arge == Integer.class || arge == Long.class;
            } else if (primitiveType == long.class) {
                return arge == Integer.class || arge == Long.class;
            } else if (primitiveType == boolean.class) {
                return arge == Boolean.class;
            } else if (primitiveType == float.class) {
                return arge == Float.class || arge == Double.class;
            } else if (primitiveType == double.class) {
                return arge == Float.class || arge == Double.class;
            } else if (primitiveType == char.class) {
                return arge == Character.class;
            } else if (primitiveType == byte.class) {
                return arge == Integer.class || arge == Long.class;
            } else {
                return false;
            }
        }
    }
}
