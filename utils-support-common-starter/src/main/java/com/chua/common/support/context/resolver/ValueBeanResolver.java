package com.chua.common.support.context.resolver;


import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.value.BeanValue;

import java.lang.reflect.Field;

/**
 * 值适配器
 *
 * @author CH
 */
public interface ValueBeanResolver {
    /**
     * 获取表达式
     *
     * @param field   字段
     * @param context
     * @return 表达式
     */
    BeanValue getExpression(Field field, ConfigurableBeanFactory context);
}
