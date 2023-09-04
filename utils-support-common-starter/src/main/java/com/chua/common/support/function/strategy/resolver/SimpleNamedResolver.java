package com.chua.common.support.function.strategy.resolver;


import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;

import java.lang.annotation.Annotation;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 名称解析器
 *
 * @author CH
 */
public class SimpleNamedResolver implements NamedResolver {
    @Override
    public String[] resolve(NamePair pair) {
        List<String> rs = new LinkedList<>();
        Annotation annotation = null == pair.getAnnotation() ?
                ClassUtils.getDeclaredAnnotation(pair.getType(), pair.getAnnotationType()) : pair.getAnnotation();
        if (null == annotation) {
            return EMPTY_ARRAY;
        }

        Set<String> attribute = new HashSet<>(pair.getAttribute());
        if (attribute.isEmpty()) {
            attribute.add("name");
            attribute.add("value");
        }
        Map<String, Object> stringObjectMap = AnnotationUtils.asMap(annotation);
        MapUtils.filterNone(stringObjectMap, (SafeConsumer<Object>) o -> {
            if (o.getClass().isArray()) {
                rs.addAll(Arrays.asList(Converter.convertIfNecessary(o, String[].class)));
                return;
            }
            rs.add(o.toString());
        }, attribute.toArray(EMPTY_ARRAY));
        return rs.toArray(EMPTY_ARRAY);
    }
}