package com.chua.common.support.task.cache;

import java.lang.annotation.*;

/**
 * cache
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cache {
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
    String key() default "";

    /**
     * 缓存时间(s), 相同缓存名称由第一个创建的缓存名称决定
     *
     * @return 缓存时间(s)
     */
    String timeout() default "1min";

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "guava";
}
