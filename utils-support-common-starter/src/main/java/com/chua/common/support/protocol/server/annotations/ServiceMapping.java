package com.chua.common.support.protocol.server.annotations;

import java.lang.annotation.*;

/**
 * 监听
 *
 * @author CH
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceMapping {
    /**
     * 监听名称
     *
     * @return 监听名称
     */
    String[] value();

    /**
     * 生产者信息
     *
     * @return 生产者信息
     */
    String produces() default "json";

    /**
     * 优先级
     *
     * @return 优先级
     */
    int sort() default 0;
}
