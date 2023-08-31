package com.chua.common.support.resource.finder;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ClasspathHelper;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.ClassResource;
import com.chua.common.support.resource.resource.Resource;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.URL_PROTOCOL_FILE;
import static com.chua.common.support.reflection.reflections.scanners.Scanners.SubTypes;


/**
 * 子类查询器
 * @author CH
 */
public class SubtypeResourceFinder extends AbstractResourceFinder {


    public SubtypeResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        Reflections reflections = getReflections(configuration, classLoader, SubTypes, name);

        Set<Resource> resources = new LinkedHashSet<>();
        Collection<Set<String>> values = reflections.getStore().get(SubTypes.name()).values();
        for (Set<String> value : values) {
            value.forEach(k -> {
                resources.add(new ClassResource(classLoader, k));
            });
        }

        return resources;
    }

    public static Reflections getReflections(
                                             ResourceConfiguration configuration,
                                             ClassLoader classLoader,
                                             Scanners subTypes,
                                             String name) {
        URL resource = classLoader.getResource(name.replace(".", "/"));
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setParallel(configuration.isParallel())
                .setExpandSuperTypes(false)
                .setScanners(subTypes)
                .setClassLoaders(new ClassLoader[]{classLoader});
        if (URL_PROTOCOL_FILE.equals(resource.getProtocol())) {
            configurationBuilder.setUrls(resource);
        } else {
            configurationBuilder.filterInputsBy(s -> s.startsWith(name)).setUrls(ClasspathHelper.forPackage(name, classLoader));
        }
        return new Reflections(configurationBuilder);
    }
}
