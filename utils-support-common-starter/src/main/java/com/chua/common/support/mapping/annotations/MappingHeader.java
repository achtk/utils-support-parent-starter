package com.chua.common.support.mapping.annotations;

import com.chua.common.support.mapping.condition.MappingCondition;

import java.lang.annotation.*;

/**
 * http消息头
 *
 * @author CH
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MappingHeaders.class)
public @interface MappingHeader {
    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 值
     *
     * @return 值
     */
    String value() default "";

    /**
     * 值
     *
     * @return 值
     */
    String script() default "";

    /**
     * 条件
     *
     * @return 值
     */
    Class<?> conditionType() default MappingCondition.class;


    /**
     * 参数类型
     * @return 参数类型
     *
     */
    MappingParam.ParamType type() default MappingParam.ParamType.NONE;
}
