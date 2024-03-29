package com.chua.common.support.database.orm;

import java.util.Locale;

import static com.chua.common.support.constant.NameConstant.*;

/**
 * @author Clinton Begin
 */
public final class PropertyNames {

    private PropertyNames() {
        // Prevent Instantiation of Static Class
    }

    public static String methodToProperty(String name) {
        if (name.startsWith(IS)) {
            name = name.substring(2);
        } else if (name.startsWith(METHOD_GETTER) || name.startsWith(METHOD_SETTER)) {
            name = name.substring(3);
        } else {
            throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        boolean b = name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)));
        if (b) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    public static boolean isProperty(String name) {
        return isGetter(name) || isSetter(name);
    }

    public static boolean isGetter(String name) {
        return (name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2);
    }

    public static boolean isSetter(String name) {
        return name.startsWith("set") && name.length() > 3;
    }

}