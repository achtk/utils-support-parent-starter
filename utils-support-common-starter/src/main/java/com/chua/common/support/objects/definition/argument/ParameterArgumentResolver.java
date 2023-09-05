package com.chua.common.support.objects.definition.argument;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.scanner.annotations.AutoInject;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import java.lang.reflect.Parameter;

/**
 * 自变量解析程序
 *
 * @author CH
 * @since 2023/09/03
 */
public interface ParameterArgumentResolver {

    /**
     * 解析
     *
     * @param parameter                   参数
     * @param typeDefinitionSourceFactory 类型定义源工厂
     * @return {@link Object}
     */
    Object resolve(Parameter parameter, TypeDefinitionSourceFactory typeDefinitionSourceFactory);


    @Spi("default")
    public static class DefaultParameterArgumentResolver implements ParameterArgumentResolver{

        @Override
        public Object resolve(Parameter parameter, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
            String name = parameter.getName();
            Class<?> type = parameter.getType();
            Object bean = typeDefinitionSourceFactory.getBean(name, type);
            if(null != bean) {
                return bean;
            }

            AutoInject autoInject = parameter.getDeclaredAnnotation(AutoInject.class);
            if(null != autoInject) {
                bean = typeDefinitionSourceFactory.getBean(autoInject.value(), type);
                if(null != bean) {
                    return bean;
                }
            }
            return typeDefinitionSourceFactory.getBean(type).get();
        }
    }
}
