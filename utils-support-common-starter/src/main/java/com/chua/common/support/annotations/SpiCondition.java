package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * spi条件注释
 *
 * @author CH
 * @see Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface SpiCondition {
    /**
     * 依赖的类
     *
     * @return 依赖的类
     */
    String[] value() default {};

    /**
     * 依赖的类
     *
     * @return 依赖的类
     */
    Class<? extends Condition>[] onCondition() default {};


    /**
     * 条件
     * @author CH
     */
    public interface Condition {
        /**
         * 是否通过
         * @return 是否通过
         */
        boolean isCondition();
    }

}
