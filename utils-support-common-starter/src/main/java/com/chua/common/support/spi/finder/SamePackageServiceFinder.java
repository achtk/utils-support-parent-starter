package com.chua.common.support.spi.finder;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ClasspathHelper;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.spi.ServiceDefinition;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.chua.common.support.constant.CommonConstant.FILE;

/**
 * 同包/子包
 *
 * @author CH
 */
public class SamePackageServiceFinder extends AbstractServiceFinder {
    @Override
    protected List<ServiceDefinition> find() {
        Package aPackage = service.getPackage();
        String packageName = null != aPackage ? aPackage.getName() : null;
        if (null == packageName) {
            packageName = service.getTypeName();
        }

        if (null == packageName) {
            return Collections.emptyList();
        }

        List<ServiceDefinition> result = new ArrayList<>();
        List<Class<?>> subTypeByPackage = findSubTypeByPackage(packageName);
        for (Class<?> aClass : subTypeByPackage) {
            if (!service.isAssignableFrom(aClass) || aClass.isInterface() || Modifier.isAbstract(aClass.getModifiers())) {
                continue;
            }
            List<ServiceDefinition> serviceDefinitions = buildDefinition(aClass);
            if (!serviceDefinitions.isEmpty()) {
                result.addAll(serviceDefinitions);
                continue;
            }
            result.addAll(buildDefinition(null, aClass, aClass.getTypeName(), null));
        }

        return result;
    }


    /**
     * 从包package中获取所有的Class
     *
     * @param packageName 包
     * @return Class
     */
    public List<Class<?>> findSubTypeByPackage(String packageName) {
        String packageDirName = packageName.replace('.', '/');

        URL resource = getClassLoader().getResource(packageDirName);
        if (null == resource) {
            return Collections.emptyList();
        }


        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setParallel(true)
                .setExpandSuperTypes(true)
                .setScanners(Scanners.SubTypes)
                .setClassLoaders(new ClassLoader[]{getClassLoader()});
        if(FILE.equals(resource.getProtocol())) {
            configurationBuilder.setUrls(resource);
        } else {
            configurationBuilder.filterInputsBy(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.startsWith(packageDirName);
                }
            }).setUrls(ClasspathHelper.forPackage(packageDirName, getClassLoader()));
        }
        Reflections reflections = new Reflections(configurationBuilder);
        Set subTypesOf = reflections.getSubTypesOf(service);
        return new ArrayList<>(subTypesOf);
    }

}
