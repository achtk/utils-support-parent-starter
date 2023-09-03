package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.classloader.ZipClassLoader;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        sourceMap.remove(name);
        System.gc();
    }

    @Override
    public void register(TypeDefinition definition) {
        register(definition.getName(), (ZipClassLoader) definition.getClassLoader());
    }

    /**
     * 注册
     *
     * @param path        路径
     * @param classLoader 类加载器
     */
    public void register(String path, ZipClassLoader classLoader) {
        if (sourceMap.containsKey(path)) {
            unregister(path);
        }
        log.info("安装>>>> {}", path);
        sourceMap.put(path, new ClassLoaderTypeDefinitionSource(path, classLoader));
    }

    @Override
    public void afterPropertiesSet() {
        String[] strings = configuration.outSide();
        if (null == strings) {
            return;
        }

        for (String s : strings) {
            register(s, null);
        }
    }


}
