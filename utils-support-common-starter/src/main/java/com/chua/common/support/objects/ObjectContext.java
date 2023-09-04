package com.chua.common.support.objects;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.objects.definition.MethodTypeDefinition;
import com.chua.common.support.objects.definition.ObjectTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.utils.ClassUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.chua.common.support.constant.NameConstant.MAVEN_TYPE_DEFINITION;
import static com.chua.common.support.constant.NameConstant.POM;

/**
 * 对象管理器
 *
 * @author CH
 */
public interface ObjectContext {

    /**
     * 获取bean
     *
     * @param name       名称
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T> T getBean(String name, Class<T> targetType);

    /**
     * 获取bean
     *
     * @param name 名称
     * @return {@link Object}
     */
    BeanObject getBean(String name);

    /**
     * 获取bean定义
     *
     * @param name 名称
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBeanDefinition(String name);

    /**
     * 获取bean
     *
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T> ObjectProvider<T> getBean(Class<T> targetType);

    /**
     * 获取bean属于类型
     *
     * @param targetType 目标类型
     * @return {@link Map}<{@link String}, {@link T}>
     */
    <T> Map<String, T> getBeanOfType(Class<T> targetType);

    /**
     * 注销
     *
     * @param typeDefinition 定义
     */
    void unregister(TypeDefinition typeDefinition);

    /**
     * 注销
     *
     * @param name 名称
     */
    void unregister(String name);

    /**
     * 注册
     *
     * @param definition 释义
     * @return {@link TypeDefinition}
     */
    TypeDefinition register(TypeDefinition definition);


    /**
     * 注册bean
     *
     * @param bean bean
     * @return {@link TypeDefinition}
     */
    default TypeDefinition registerBean(Object bean) {
        if (bean instanceof TypeDefinition) {
            return register((TypeDefinition) bean);
        }

        if (bean instanceof Method) {
            return register(new MethodTypeDefinition((Method) bean));
        }

        return register(new ObjectTypeDefinition(bean.getClass().getTypeName(), bean));
    }

    /**
     * 注册
     *
     * @param file       释义
     * @param repository repository
     * @return {@link TypeDefinition}
     */
    default TypeDefinition register(File file, String repository) {
        TypeDefinition typeDefinition = ClassUtils.forObject(MAVEN_TYPE_DEFINITION, file, repository, null);
        if (typeDefinition instanceof InitializingAware) {
            ((InitializingAware) typeDefinition).afterPropertiesSet();
        }
        return register(typeDefinition);
    }

    /**
     * 注册
     *
     * @param file 释义
     * @return {@link TypeDefinition}
     */
    default TypeDefinition register(File file) {
        if (ClassUtils.isPresent(MAVEN_TYPE_DEFINITION)) {
            Zip zip = new Zip();
            AtomicReference<InputStream> stream = new AtomicReference<>();
            try (InputStream fis = Files.newInputStream(file.toPath())) {
                zip.unFile(fis, fileMedia -> {
                    if (fileMedia.getName().endsWith(POM)) {
                        stream.set(fileMedia.getStream());
                        return true;
                    }
                    return false;
                }, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            InputStream inputStream = stream.get();
            TypeDefinition typeDefinition = ClassUtils.forObject(MAVEN_TYPE_DEFINITION, file, inputStream);
            if (typeDefinition instanceof InitializingAware) {
                ((InitializingAware) typeDefinition).afterPropertiesSet();
            }
            return register(typeDefinition);
        }
        return register(new ZipTypeDefinition(file));
    }

    /**
     * 获取带有注解的方法
     *
     * @param annotationType 类型
     * @return 结果
     */
    Map<String, TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType);
}
