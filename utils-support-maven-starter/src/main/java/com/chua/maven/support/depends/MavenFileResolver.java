package com.chua.maven.support.depends;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.depends.GrapeFileResolver;
import com.chua.common.support.lang.depends.SimplePathSurroundings;
import com.chua.common.support.lang.depends.Surroundings;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.jcabi.aether.Aether;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 解释器
 * @author CH
 */
@Spi({"default", "maven"})
public class MavenFileResolver implements GrapeFileResolver {

    @Override
    public Surroundings resolve(InputStream stream, String cachePath, ClassLoader parentClassLoader, File original) {
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

        List<Artifact> cache = new LinkedList<>();
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(stream);
        } catch (Exception ignored) {
        }

        Parent parent = model.getParent();
        Map<String, String> properties = createProfile(project, model, cachePath, cache);
        for (Dependency dependency : model.getDependencies()) {
            try {
                List<Artifact> resolve = new Aether(project, new File(cachePath)).resolve(
                        new DefaultArtifact(
                                dependency.getGroupId() + ":" +
                                        dependency.getArtifactId() + ":" +
                                        (StringUtils.isNullOrEmpty(dependency.getClassifier()) ?
                                                StringUtils.isNullOrEmpty(dependency.getVersion()) ? analysis(parent.getVersion(), properties) : analysis(dependency.getVersion(), properties) :
                                                (dependency.getClassifier() + ":" + analysis(dependency.getVersion(), properties))), properties), StringUtils.defaultString(dependency.getScope(), "runtime")
                );
                cache.addAll(resolve);
            } catch (DependencyResolutionException e) {
                throw new RuntimeException(e);
            }

        }
        List<URL> list = new ArrayList<>();
        try {
            list.add(original.toURI().toURL());
        } catch (MalformedURLException ignored) {
        }
        for (Artifact artifact : cache) {
            File file = artifact.getFile();
            try {
                list.add(file.toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }

        return new SimplePathSurroundings(list.toArray(new URL[0]), Collections.emptyMap(), parentClassLoader);

    }

    private String analysis(String version, Map<String, String> properties) {
        return properties.getOrDefault(version.replace("${", "").replace("}", ""), version);
    }

    private Map<String, String> createProfile(MavenProject project, Model model, String cachePath, List<Artifact> cache) {
        Map<String, String> rs = new LinkedHashMap<>(MapUtils.asStringMap(model.getProperties()));
        Parent parent = model.getParent();
        if(null != parent) {
            rs.putAll(createProfile(project, parent, cachePath, cache));
        }

        return rs;
    }

    private Map<String, String> createProfile(MavenProject project, Parent parent, String cachePath, List<Artifact> cache) {
        Map<String, String> rs = new LinkedHashMap<>();
        File file = new File(cachePath, parent.getGroupId().replace(".", "/") + "/" +
                parent.getArtifactId().replace(".", "/") + "/" +
                parent.getVersion() + "/" + parent.getArtifactId() + "-" + parent.getVersion() + ".pom");
        if(file.exists()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = null;
            try (FileInputStream fileInputStream = new FileInputStream(file)){
                model = reader.read(fileInputStream);
                rs.putAll(MapUtils.asStringMap(model.getProperties()));
                for (Dependency dependency : model.getDependencies()) {
                    try {
                        List<Artifact> resolve = new Aether(project, new File(cachePath)).resolve(
                                new DefaultArtifact(
                                        dependency.getGroupId() + ":" +
                                                dependency.getArtifactId() + ":" +
                                                (StringUtils.isNullOrEmpty(dependency.getClassifier()) ?
                                                        StringUtils.isNullOrEmpty(dependency.getVersion()) ? parent.getVersion() : dependency.getVersion() :
                                                        (dependency.getClassifier() + ":" + dependency.getVersion())), rs), StringUtils.defaultString(dependency.getScope(), "runtime")
                        );
                        cache.addAll(resolve);
                    } catch (DependencyResolutionException e) {
                        throw new RuntimeException(e);
                    }

                }
                return createProfile(project, model, cachePath, cache);
            } catch (Exception ignored) {
            }
        }
        return rs;
    }
}
