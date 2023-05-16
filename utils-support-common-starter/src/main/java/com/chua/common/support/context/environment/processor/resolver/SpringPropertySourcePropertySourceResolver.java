package com.chua.common.support.context.environment.processor.resolver;

import com.chua.common.support.context.environment.property.MultiPropertySource;
import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.process.AbstractAnnotationBeanPostProcessor;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.MapUtils;

import java.lang.annotation.Annotation;
import java.util.*;

import static com.chua.common.support.context.constant.ContextConstant.PROPERTY_SOURCE;


/**
 * 解释器
 *
 * @author CH
 */
public class SpringPropertySourcePropertySourceResolver implements PropertySourceResolver {
    @Override
    public List<PropertySource> getPropertySources(ApplicationContextConfiguration contextConfiguration) {
        if (null == PROPERTY_SOURCE) {
            return Collections.emptyList();
        }

        Reflections reflections = AbstractAnnotationBeanPostProcessor.getInstance(contextConfiguration);
        if(null == reflections) {
            return Collections.emptyList();
        }
        List<PropertySource> rs = new LinkedList<>();

        Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(PROPERTY_SOURCE);

        for (Class<?> aClass : annotatedWith) {
            Annotation annotation1 = aClass.getDeclaredAnnotation(PROPERTY_SOURCE);
            if (null != annotation1) {
                Map<String, Object> stringObjectMap = AnnotationUtils.asMap(annotation1);
                String[] values = MapUtils.getStringArray(stringObjectMap, "value");
                doAnalysis(rs, values);
            }
        }
        return rs;
    }

    /**
     * 分析数据
     *
     * @param rs     结果
     * @param values 值
     */
    private void doAnalysis(List<PropertySource> rs, String[] values) {
        for (String value : values) {
            doAnalysis(rs, value);
        }
    }

    /**
     * 分析数据
     *
     * @param rs    结果
     * @param value 值
     */
    private void doAnalysis(List<PropertySource> rs, String value) {
        ResourceProvider resourceProvider = ResourceProvider.of(value);
        Set<Resource> resources = resourceProvider.getResources();
        doAnalysis(rs, resources);
    }

    /**
     * 分析数据
     *
     * @param rs        结果
     * @param resources 值
     */
    private void doAnalysis(List<PropertySource> rs, Set<Resource> resources) {
        for (Resource resource : resources) {
            doAnalysis(rs, resource);
        }
    }

    /**
     * 分析数据
     *
     * @param rs       结果
     * @param resource 值
     */
    private void doAnalysis(List<PropertySource> rs, Resource resource) {

    }

    @Override
    public MultiPropertySource addPropertySource(PropertySource propertySource) {
        return this;
    }

    @Override
    public MultiPropertySource addPropertySource(List<PropertySource> propertySource) {
        return this;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getProperty(String name) {
        return null;
    }
}
