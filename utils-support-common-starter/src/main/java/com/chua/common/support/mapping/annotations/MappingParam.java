package com.chua.common.support.mapping.annotations;

import java.lang.annotation.*;

/**
 * http消息头
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/16
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingParam {
    /**
     * 名称
     *
     * @return 名称
     */
    String value() default "";

    /**
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 是否忽略
     *
     * @return 是否忽略
     */
    boolean ignore() default false;

}
