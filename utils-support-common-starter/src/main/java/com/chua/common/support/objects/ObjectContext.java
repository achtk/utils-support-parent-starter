package com.chua.common.support.objects;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.utils.ClassUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
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
    Object getBean(String name);

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
     */
    void register(TypeDefinition definition);

    /**
     * 注册
     *
     * @param file       释义
     * @param repository repository
     */
    default void register(File file, String repository) {
        TypeDefinition typeDefinition = ClassUtils.forObject(MAVEN_TYPE_DEFINITION, file, repository, null);
        if(typeDefinition instanceof InitializingAware) {
            ((InitializingAware) typeDefinition).afterPropertiesSet();
        }
        register(typeDefinition);
    }

    /**
     * 注册
     *
     * @param file 释义
     */
    default void register(File file) {
        if (ClassUtils.isPresent(MAVEN_TYPE_DEFINITION)) {
            Zip zip = new Zip();
            AtomicReference<InputStream> stream = new AtomicReference<>();
            try (InputStream fis = Files.newInputStream(file.toPath())) {
                zip.unFile(fis, fileMedia -> {
                    if(fileMedia.getName().endsWith(POM)) {
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
            if(typeDefinition instanceof InitializingAware) {
                ((InitializingAware) typeDefinition).afterPropertiesSet();
            }
            register(typeDefinition);
            return;
        }
        register(new ZipTypeDefinition(file));
    }

}
