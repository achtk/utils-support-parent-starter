package com.chua.common.support.context.aggregate.adaptor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.annotation.AutoService;
import com.chua.common.support.context.definition.AggregateDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 聚合体对象适配器
 * @author CH
 */
@Spi("delegate")
public class DelegateAggregateAdaptor implements AnnotationAggregateAdaptor{
    @Override
    public Set<Class<? extends Annotation>> getScanAnnotation() {
        return CollectionUtils.newHashSet(AutoService.class);
    }

    @Override
    public TypeDefinition<?> createDefinition(Aggregate aggregate, Class<? extends Annotation> annotationType, Class<?> type) {
        AutoService autoService = type.getDeclaredAnnotation(AutoService.class);
        if(null != autoService) {
            return new AggregateDefinition<>(aggregate, type, autoService.value());
        }
        return null;
    }

}
