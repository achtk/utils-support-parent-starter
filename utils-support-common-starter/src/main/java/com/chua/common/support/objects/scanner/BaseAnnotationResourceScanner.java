package com.chua.common.support.objects.scanner;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
public abstract class BaseAnnotationResourceScanner<T extends Annotation> extends AbstractResourceScanner {
    private static Reflections REFLECTIONS;

    public BaseAnnotationResourceScanner(String[] packages) {
        super(packages);
    }

    @Override
    public Set<Class<?>> scan() {
        initial(packages);

        if (null == REFLECTIONS) {
            return Collections.emptySet();
        }

        return REFLECTIONS.getTypesAnnotatedWith(getAnnotation());
    }

    /**
     * 获取实例
     * 获取注解器
     *
     * @param packages 配置
     * @return Reflections
     */
    public static Reflections getInstance(String[] packages) {
        if (null != REFLECTIONS) {
            return REFLECTIONS;
        }

        initial(packages);
        return REFLECTIONS;
    }

    /**
     * 初始化
     *
     * @param packages 扫描位置
     */
    private static void initial(String[] packages) {
        if (null != REFLECTIONS) {
            return;
        }


        if (null == packages || packages.length == 0) {
            initialScan(null);
            return;
        }

        initialScan(packages);
    }

    /**
     * 初始化
     *
     * @param packages 包
     */
    private static void initialScan(String[] packages) {
        ConfigurationBuilder builder = ConfigurationBuilder.build();
        if (null != packages) {
            builder.forPackages(packages);
        } else {
            builder.setUrls(new ArrayList<>());
            builder.forPackages(".");
        }


        builder.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
                .setParallel(true)
                .addClassLoaders(ClassLoader.getSystemClassLoader());


        REFLECTIONS = new Reflections(builder);
    }

    /**
     * 获取注解
     *
     * @return 注解
     */
    Class<T> getAnnotation() {
        return (Class<T>) ClassUtils.getActualTypeArguments(this.getClass())[0];
    }

    /**
     * 匹配
     *
     * @param aClass 班级
     * @return boolean
     */
    public boolean isMatch(Class<?> aClass) {
        return AnnotationUtils.hasAnnotation(aClass, getAnnotation());
    }
}
