package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * DefaultValue
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
