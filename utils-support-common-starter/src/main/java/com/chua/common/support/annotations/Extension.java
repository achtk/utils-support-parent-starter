package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * 扩展
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Extension {
    /**
     * 名称
     *
     * @return 名称
     */
    String value() ;
}
