package com.chua.common.support.reflection.dynamic.attribute;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Modifier;

/**
 * field
 *
 * @author CH
 */
@Data
@Builder
public class ConstructAttribute {
    @Builder.Default
    private int modifiers = Modifier.PUBLIC;
    @Builder.Default
    private String[] argTypes = new String[0];
    @Builder.Default
    private String[] exceptionTypes = new String[0];
    @Builder.Default
    private AnnotationAttribute[] annotationTypes = new AnnotationAttribute[0];
}
