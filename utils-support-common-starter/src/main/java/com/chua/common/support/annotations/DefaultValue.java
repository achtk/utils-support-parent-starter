package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * 超时
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface DefaultValue {
    /**
     * 默认值
     *
     * @return 默认值
     */
    String value();
}
