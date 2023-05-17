package com.chua.common.support.file.export;

import java.lang.annotation.*;

/**
 * excel field
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ExportProperty {

    String value();

    String format() default "";

    boolean ignore() default false;
}
