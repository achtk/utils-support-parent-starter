package com.chua.common.support.reflection.describe.factory;

import com.chua.common.support.reflection.describe.AnnotationDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.reflection.describe.executor.MethodExecutor;
import com.chua.common.support.reflection.describe.processor.MethodAnnotationPostProcessor;
import com.chua.common.support.reflection.describe.processor.ParameterAnnotationPostProcessor;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解工厂处理
 *
 * @author CH
 */
public interface MethodAnnotationFactory {
    /**
     * 注解工厂
     *
     * @param methodDescribe 方法描述
     * @return this
     */
    static MethodAnnotationFactory create(MethodDescribe methodDescribe) {
        return new SimpleMethodAnnotationFactory(methodDescribe);
    }

    /**
     * 执行方法
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    Object execute(Object entity, Object... args);

    /**
     * 默认实现
     */
    @SuppressWarnings("ALL")
    final class SimpleMethodAnnotationFactory implements MethodAnnotationFactory {

        private final MethodDescribe methodDescribe;
        private final List<MethodAnnotationPostProcessor> processors = new LinkedList<>();
        private final List<ParameterAnnotationPostProcessor> parameters = new LinkedList<>();
        static final Map<String, MethodAnnotationPostProcessor> CACHE = new ConcurrentHashMap<>();
        static final Map<String, ParameterAnnotationPostProcessor> PARAM_CACHE = new ConcurrentHashMap<>();

        static {
            ServiceProvider<MethodAnnotationPostProcessor> serviceProvider = ServiceProvider.of(MethodAnnotationPostProcessor.class);
            serviceProvider.forEach((k, v) -> {
                CACHE.put(k, v);
                Class<? extends MethodAnnotationPostProcessor> aClass = v.getClass();
                Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(aClass);
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                    Type type = actualTypeArguments[0];
                    CACHE.put(type.getTypeName(), v);
                }
            });
            ServiceProvider<ParameterAnnotationPostProcessor> serviceProvider1 = ServiceProvider.of(ParameterAnnotationPostProcessor.class);
            serviceProvider1.forEach((k, v) -> {
                PARAM_CACHE.put(k, v);
                Class<? extends ParameterAnnotationPostProcessor> aClass = v.getClass();
                Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(aClass);
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                    Type type = actualTypeArguments[0];
                    PARAM_CACHE.put(type.getTypeName(), v);
                }
            });
        }

        public SimpleMethodAnnotationFactory(MethodDescribe methodDescribe) {
            this.methodDescribe = methodDescribe;
            AnnotationDescribe[] annotationDescribes = methodDescribe.annotationTypes();
            for (AnnotationDescribe annotationDescribe : annotationDescribes) {
                String name = annotationDescribe.getName();
                MethodAnnotationPostProcessor methodAnnotationPostProcessor = CACHE.get(name);
                if (null == methodAnnotationPostProcessor) {
                    continue;
                }

                processors.add(methodAnnotationPostProcessor);
            }

            ParameterDescribe[] parameterDescribes = methodDescribe.parameterDescribes();
            for (ParameterDescribe parameterDescribe : parameterDescribes) {
                AnnotationDescribe[] annotationDescribes1 = parameterDescribe.annotationTypes();
                for (AnnotationDescribe annotationDescribe : annotationDescribes1) {
                    String name = annotationDescribe.getName();
                    ParameterAnnotationPostProcessor parameterAnnotationPostProcessor = PARAM_CACHE.get(name);
                    if (null == parameterAnnotationPostProcessor) {
                        continue;
                    }

                    parameters.add(parameterAnnotationPostProcessor);
                }
            }
        }

        @Override
        public Object execute(Object entity, Object... args) {
            return MethodExecutor.of(processors, methodDescribe)
                    .execute(entity, ParameterAnnotationFactory.of(parameters).execute(methodDescribe, args));
        }
    }
}