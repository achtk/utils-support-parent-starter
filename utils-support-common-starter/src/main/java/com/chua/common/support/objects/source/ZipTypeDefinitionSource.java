package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.classloader.ZipClassLoader;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.NameConstant.MAVEN_TYPE_DEFINITION;
import static com.chua.common.support.objects.source.AbstractTypeDefinitionSource.COMPARABLE;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
@SuppressWarnings("ALL")
@Slf4j
@Spi("zip")
public class ZipTypeDefinitionSource implements TypeDefinitionSource, InitializingAware {

    private final ConfigureContextConfiguration configuration;
    private Map<String, ClassLoaderTypeDefinitionSource> sourceMap = new ConcurrentHashMap<>();

    public ZipTypeDefinitionSource(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
        afterPropertiesSet();
    }

    @Override
    public boolean isMatch(TypeDefinition typeDefinition) {
        return typeDefinition instanceof ZipTypeDefinition;
    }

    @Override
    public SortedList<TypeDefinition> getBean(String name, Class<?> targetType) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (ClassLoaderTypeDefinitionSource source : sourceMap.values()) {
            rs.addAll(source.getBean(name, targetType));
        }
        return rs;
    }

    @Override
    public SortedList<TypeDefinition> getBean(String name) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (ClassLoaderTypeDefinitionSource source : sourceMap.values()) {
            rs.addAll(source.getBean(name));
        }
        return rs;
    }

    @Override
    public SortedList<TypeDefinition> getBean(Class<?> targetType) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (ClassLoaderTypeDefinitionSource source : sourceMap.values()) {
            rs.addAll(source.getBean(targetType));
        }
        return rs;
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (ClassLoaderTypeDefinitionSource source : sourceMap.values()) {
            source.unregister(typeDefinition);
        }
    }


    @Override
    public void unregister(String name) {
        name = new File(name).getAbsolutePath();
        ClassLoaderTypeDefinitionSource classLoaderTypeDefinitionSource = sourceMap.get(name);
        if(null != classLoaderTypeDefinitionSource) {
            classLoaderTypeDefinitionSource.close();
        }
        sourceMap.remove(name);
    }

    @Override
    public void register(TypeDefinition definition) {
        register(definition.getName(), definition.getDepends(), (ZipClassLoader) definition.getClassLoader());
    }

    @Override
    public SortedList<TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType) {
        return SortedList.emptyList();
    }

    /**
     * 登记
     * 注册
     *
     * @param path        路径
     * @param classLoader 类加载器
     * @param urls        url
     */
    public void register(String[] paths, List<URL> urls, ZipClassLoader classLoader) {
        for (String path : paths) {
            if (sourceMap.containsKey(path)) {
                unregister(path);
            }
            File file = new File(path);
            sourceMap.put(file.getAbsolutePath(), new ClassLoaderTypeDefinitionSource(path, urls, classLoader, configuration.outSideInAnnotation()));
        }
    }

    @Override
    public void afterPropertiesSet() {
        String[] strings = configuration.outSide();
        if (null == strings) {
            return;
        }

        for (String s : strings) {
            TypeDefinition typeDefinition = ClassUtils.forObject(MAVEN_TYPE_DEFINITION, s, configuration.repository(), null);
            if(typeDefinition instanceof InitializingAware) {
                ((InitializingAware) typeDefinition).afterPropertiesSet();
            }
            register(typeDefinition);
        }
    }


}
