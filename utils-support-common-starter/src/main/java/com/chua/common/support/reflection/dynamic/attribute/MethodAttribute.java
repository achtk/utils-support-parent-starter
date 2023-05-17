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
public class MethodAttribute {
    @Builder.Default
    private int modifiers = Modifier.PUBLIC;
    private String name;
    @Builder.Default
    private String returnType = Object.class.getTypeName();
    @Builder.Default
    private String[] argTypes = new String[0];
    private String body;
    @Builder.Default
    private String[] exceptionTypes = new String[0];
    @Builder.Default
    private AnnotationAttribute[] annotationTypes = new AnnotationAttribute[0];
}
