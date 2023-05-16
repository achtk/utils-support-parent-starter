package com.chua.common.support.lang.depends;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * grape
 * @author CH
 */
@AllArgsConstructor
public class GrapeType implements InitializingAware {


    private Class<?>[] type;

    private Map<String, ArtifactRepository> remote = new ConcurrentHashMap<>();

    private List<Dependency> dependencies = new LinkedList<>();
    private List<DependencyExclude> dependenciesExclude = new LinkedList<>();

    private GrapeResolver resolver = ServiceProvider.of(GrapeResolver.class).getNewExtension("default");



    public GrapeType(Class<?>... type) {
        this.type = type;
        this.analysis();
        afterPropertiesSet();
    }

    /**
     * 分析
     */
    private void analysis() {
        analysisRemote();
        analysisDependency();
        analysisDependencyExclude();
    }

    /**
     * 去除依赖
     */
    private void analysisDependencyExclude() {
        for (Class<?> aClass : type) {
            DependencyExclude dependencyExclude = aClass.getDeclaredAnnotation(DependencyExclude.class);
            if(null == dependencyExclude) {
                continue;
            }

            this.dependenciesExclude.add(dependencyExclude);

        }


        for (Class<?> aClass : type) {
            DependencyExcludeCollect dependencyExcludeCollect = aClass.getDeclaredAnnotation(DependencyExcludeCollect.class);
            if(null == dependencyExcludeCollect) {
                continue;
            }

            this.dependenciesExclude.addAll(Arrays.asList(dependencyExcludeCollect.value()));

        }
    }

    /**
     * 分析依赖
     */
    private void analysisDependency() {
        for (Class<?> aClass : type) {
            Dependency dependency = aClass.getDeclaredAnnotation(Dependency.class);
            if(null == dependency) {
                continue;
            }

            this.dependencies.add(dependency);

        }


        for (Class<?> aClass : type) {
            Dependencies dependency = aClass.getDeclaredAnnotation(Dependencies.class);
            if(null == dependency) {
                continue;
            }

            this.dependencies.addAll(Arrays.asList(dependency.value()));

        }
    }

    /**
     * 分析下载地址
     */
    private void analysisRemote() {
        for (Class<?> aClass : type) {
            DependencyResolver declaredAnnotation = aClass.getDeclaredAnnotation(DependencyResolver.class);
            if(null == declaredAnnotation) {
                continue;
            }

            AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(declaredAnnotation, false);
            remote.put(annotationAttributes.getString("name"), new SimpleArtifactRepository(annotationAttributes.getString("name"), declaredAnnotation.root(), declaredAnnotation.cachePath()));
        }

    }


    @Override
    public void afterPropertiesSet() {
        if(remote.isEmpty()) {
            remote.put("alibaba", new SimpleArtifactRepository("default", "https://maven.aliyun.com/repository/public", "../cache"));
        }
    }

    /**
     * 执行
     */
    public Surroundings execute() {
        return resolver.resolve(Collections.unmodifiableList(new ArrayList<>(remote.values())), dependencies, dependenciesExclude);
    }
}
