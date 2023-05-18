package com.chua.maven.support.depends;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.lang.depends.*;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.StringUtils;
import com.jcabi.aether.Aether;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解释器
 * @author CH
 */
@Spi({"default", "maven"})
public class MavenResolver implements GrapeResolver {


    @Override
    public Surroundings resolve(List<ArtifactRepository> remote, List<Dependency> dependencies, List<DependencyExclude> dependenciesExclude) {
        MavenProject project = new MavenProject();
        project.setRemoteArtifactRepositories(
                remote.stream().map(it -> {
                    return new MavenArtifactRepository(
                            it.id(),
                            it.url(),
                            new DefaultRepositoryLayout(),
                            new ArtifactRepositoryPolicy(),
                            new ArtifactRepositoryPolicy()
                    );
                }).collect(Collectors.toList()));

        String cachePath = remote.get(0).cachePath();
        List<Artifact> cache = new LinkedList<>();
        for (Dependency dependency : dependencies) {
            try {
                AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(dependency, false);
                List<Artifact> resolve = new Aether(project, new File(cachePath)).resolve(
                        new DefaultArtifact(
                                dependency.group() + ":" +
                                        annotationAttributes.get("value") + ":" +
                                        (StringUtils.isNullOrEmpty(dependency.classifier()) ?
                                                dependency.version() :
                                                (dependency.classifier() + ":" + dependency.version()))), "runtime"
                );
                cache.addAll(resolve);
            } catch (DependencyResolutionException e) {
                throw new RuntimeException(e);
            }

        }
        List<URL> list = new ArrayList<>();
        for (Artifact artifact : cache) {
            File file = artifact.getFile();
            URL toURL = null;
            try {
                toURL = file.toURI().toURL();
            } catch (MalformedURLException ignored) {
            }
            list.add(toURL);
        }
        return new SimplePathSurroundings(list.toArray(new URL[0]), Collections.emptyMap(), Thread.currentThread().getContextClassLoader());
    }
}
