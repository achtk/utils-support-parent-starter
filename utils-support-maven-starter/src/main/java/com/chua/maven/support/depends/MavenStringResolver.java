package com.chua.maven.support.depends;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.depends.GrapeStringResolver;
import com.chua.common.support.lang.depends.SimplePathSurroundings;
import com.chua.common.support.lang.depends.Surroundings;
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
import java.util.*;

/**
 * 解释器
 * @author CH
 */
@Spi({"default", "maven"})
public class MavenStringResolver implements GrapeStringResolver {

    @Override
    public Surroundings resolve(String[] args, String cachePat, ClassLoader parentClassLoader) {
        MavenProject project = new MavenProject();
        project.setRemoteArtifactRepositories(
                Arrays.asList(
                new MavenArtifactRepository(
                        "default",
                        System.getProperty("grape.default.home", "https://maven.aliyun.com/repository/public"),
                        new DefaultRepositoryLayout(),
                        new ArtifactRepositoryPolicy(),
                        new ArtifactRepositoryPolicy()
                ))
        );

        String cachePath =  System.getProperty("grape.default.home", "../cache");
        List<Artifact> cache = new LinkedList<>();
        for (String dependency : args) {
            try {
                List<Artifact> resolve = new Aether(project, new File(cachePath)).resolve(new DefaultArtifact(dependency), "runtime");
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
        return new SimplePathSurroundings(list.toArray(new URL[0]), Collections.emptyMap(), parentClassLoader);

    }
}
