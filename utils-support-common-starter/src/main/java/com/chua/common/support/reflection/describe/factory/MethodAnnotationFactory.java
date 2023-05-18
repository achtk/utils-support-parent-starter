package com.chua.common.support.reflection.describe.factory;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.annotations.Spi;
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
     * @param plugins        plugins
     * @return this
     */
    static MethodAnnotationFactory create(MethodDescribe methodDescribe, Object[] plugins) {
        return new SimpleMethodAnnotationFactory(methodDescribe, plugins);
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

        public SimpleMethodAnnotationFactory(MethodDescribe methodDescribe, Object[] plugins) {
            this.methodDescribe = methodDescribe;
            initialProcessor(plugins);
            refreshProcessor();
        }

        private void refreshProcessor() {
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

        /**
         * 初始化插件
         *
         * @param plugins
         */
        private void initialProcessor(Object[] plugins) {
            for (Object plugin : plugins) {
                if (plugin instanceof MethodAnnotationPostProcessor) {
                    initialMethodProcessor(null, (MethodAnnotationPostProcessor) plugin);
                    continue;
                }

                if (plugin instanceof ParameterAnnotationPostProcessor) {
                    initialParamterProcessor(null, (ParameterAnnotationPostProcessor) plugin);
                }
            }
        }
        /**
         * 初始化属性插件
         *@param name 名称
         * @param plugin 插件
         */
        private void initialParamterProcessor(String name, ParameterAnnotationPostProcessor plugin) {
            name = getName(name, plugin);
            if(null == name) {
                return;
            }

            PARAM_CACHE.put(name, plugin);
            Class<? extends ParameterAnnotationPostProcessor> aClass = plugin.getClass();
            Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(aClass);
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                Type type = actualTypeArguments[0];
                PARAM_CACHE.put(type.getTypeName(), plugin);
            }
        }

        /**
         * 初始化方法插件
         * @param name 名称
         * @param plugin 插件
         */
        private void initialMethodProcessor(String name, MethodAnnotationPostProcessor plugin) {
            name = getName(name, plugin);
            if(null == name) {
                return;
            }

            CACHE.put(name, plugin);
            Class<? extends MethodAnnotationPostProcessor> aClass = plugin.getClass();
            Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(aClass);
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                Type type = actualTypeArguments[0];
                CACHE.put(type.getTypeName(), plugin);
            }
        }

        /**
         * 获取插件名称
         *
         * @param name   名称
         * @param plugin 插件
         * @return 结果
         */
        private String getName(String name, Object plugin) {
            if (null != name) {
                return name;
            }

            Class<?> aClass = plugin.getClass();
            Spi spi = aClass.getDeclaredAnnotation(Spi.class);
            if (null != spi) {
                return spi.value()[0];
            }
            Extension extension = aClass.getDeclaredAnnotation(Extension.class);
            if (null != extension) {
                return extension.value();
            }
            return null;
        }

        @Override
        public Object execute(Object entity, Object... args) {
            return MethodExecutor.of(processors, methodDescribe)
                    .execute(entity, ParameterAnnotationFactory.of(parameters).execute(methodDescribe, args));
        }
    }
}