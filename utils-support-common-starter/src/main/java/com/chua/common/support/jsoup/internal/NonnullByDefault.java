package com.chua.common.support.jsoup.internal;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Administrator
 */
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.CLASS)
public @interface NonnullByDefault {
}
