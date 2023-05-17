package com.chua.common.support.reflection.dynamic.attribute;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * 注解
 *
 * @author CH
 */
@Data
@Builder
public class AnnotationAttribute {

    private String name;
    private String type;
    @Singular("param")
    private Map<String, Object> params;
}
