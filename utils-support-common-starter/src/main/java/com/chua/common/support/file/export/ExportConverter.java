package com.chua.common.support.file.export;


import com.chua.common.support.file.export.resolver.ValueResolver;

import java.lang.annotation.*;

/**
 * excel field
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ExportConverter {

    Class<? extends ValueResolver> value();
}
