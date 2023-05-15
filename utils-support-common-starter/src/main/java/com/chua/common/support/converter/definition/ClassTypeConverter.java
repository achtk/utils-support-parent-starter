package com.chua.common.support.converter.definition;


import com.chua.common.support.utils.ClassUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT;

/**
 * 类转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/26
 */
public class ClassTypeConverter implements TypeConverter<Class> {

    @Override
    public Class convert(Object value) {
        if (null == value) {
            return Void.class;
        }

        if (value instanceof String && ((String) value).contains(SYMBOL_DOT)) {
            return ClassUtils.forName(value.toString());
        }

        return value.getClass();
    }

    @Override
    public Class<Class> getType() {
        return Class.class;
    }
}
