package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * spi条件注释
 *
 * @author CH
 * @see Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiIgnore {

}
