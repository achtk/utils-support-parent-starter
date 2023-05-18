package com.chua.common.support.task.cache;

import java.lang.annotation.*;

/**
 * 缓存更新
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CachePut {
    /**
     * 缓存名称
     *
     * @return 缓存名称
     */
    String value() default "default";

    /**
     * 缓存的key
     *
     * @return 缓存的key
     */
    String key();

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "guava";
}
