package com.chua.common.support.database.annotation;


import java.lang.annotation.*;

/**
 * Annotation to declare finder queries directly on repository methods.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Christoph Strobl
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
public @interface Query {

    /**
     * Defines the JPA query to be executed when the annotated method is called.
     */
    String value() default "";

}
