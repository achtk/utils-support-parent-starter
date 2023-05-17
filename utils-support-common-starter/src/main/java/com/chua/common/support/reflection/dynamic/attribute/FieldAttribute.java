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
public class FieldAttribute {
    @Builder.Default
    private int modifiers = Modifier.PRIVATE;
    private String name;
    private String type;
    private Object value;
    @Builder.Default
    private AnnotationAttribute[] annotationTypes = new AnnotationAttribute[0];
}
