package com.chua.common.support.context.environment.processor.resolver;

import com.chua.common.support.context.annotation.ProfileSource;
import com.chua.common.support.context.environment.property.MultiPropertySource;
import com.chua.common.support.context.environment.property.ProfilePropertySource;
import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.process.AbstractAnnotationBeanPostProcessor;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.ProfileBuilder;
import com.chua.common.support.reflection.reflections.Reflections;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 解释器
 *
 * @author CH
 */
public class PropertySourcePropertySourceResolver implements PropertySourceResolver {

    final Profile profile = ProfileBuilder.newBuilder().build();


    @Override
    public List<PropertySource> getPropertySources(ApplicationContextConfiguration contextConfiguration) {
        Reflections reflections = AbstractAnnotationBeanPostProcessor.getInstance(contextConfiguration);
        if(null == reflections) {
            return Collections.emptyList();
        }

        List<PropertySource> rs = new LinkedList<>();
        Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(ProfileSource.class);

        for (Class<?> aClass : annotatedWith) {
            ProfileSource profileSource = aClass.getDeclaredAnnotation(ProfileSource.class);
            if (null != profileSource) {
                String[] values = profileSource.value();
                doAnalysis(rs, values);
            }
        }
        return null;
    }

    /**
     * 分析数据
     *
     * @param rs     结果
     * @param values 值
     */
    private void doAnalysis(List<PropertySource> rs, String[] values) {
        for (String value : values) {
            profile.addProfile(value);
        }

        rs.add(new ProfilePropertySource(profile));
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
