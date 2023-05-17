package com.chua.common.support.jsoup.internal;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.CLASS)
public @interface NonnullByDefault {
}
