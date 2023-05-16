package com.chua.common.support.lang.depends;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 依赖
 * @author CH
 */
@Documented
@Target({ElementType.PACKAGE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.LOCAL_VARIABLE,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface DependencyResolver {
    /**
     * Allows a shorthand form which sets the name and root to this value.
     * Must not be used if name() or root() is non-empty.
     */
    @AliasFor("name")
    String value() default "";
    /**
     * Allows a shorthand form which sets the name and root to this value.
     * Must not be used if name() or root() is non-empty.
     */
    @AliasFor("value")
    String name() default "";
    /**
     * Allows a shorthand form which sets the name and root to this value.
     * Must not be used if name() or root() is non-empty.
     */
    String cachePath() default "./cache";

    /**
     * The URL for a repo containing the grape/artifact.
     * A non-empty value is required unless value() is used.
     */
    String root() default "";

}
