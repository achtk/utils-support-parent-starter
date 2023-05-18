package com.chua.common.support.task.cache;

import java.lang.annotation.*;

/**
 * 缓存清除
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheEvict {
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
     * 删除全部实体
     *
     * @return 删除全部实体
     */
    boolean allEntries() default false;

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "guava";
}
