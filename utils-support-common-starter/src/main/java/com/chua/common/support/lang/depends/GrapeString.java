package com.chua.common.support.lang.depends;

import com.chua.common.support.spi.ServiceProvider;

/**
 * grape
 * @author CH
 */
public class GrapeString {


    private String dependencyPath;
    private ClassLoader parentClassLoader;
    private String[] dependency;

    public GrapeString(String dependencyPath, ClassLoader parentClassLoader, String... dependency) {
        this.dependencyPath = dependencyPath;
        this.parentClassLoader = parentClassLoader;
        this.dependency = dependency;
    }

    /**
     * 执行
     */
    public Surroundings execute() {
        return ServiceProvider.of(GrapeStringResolver.class).getNewExtension("default").resolve(dependency, dependencyPath, parentClassLoader);
    }
}
