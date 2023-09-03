package com.chua.maven.support.depends.definition;

import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.utils.StringUtils;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;

import java.io.File;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * maven定义
 *
 * @author CH
 * @since 2023/09/03
 */
public class MavenTypeDefinition extends ZipTypeDefinition {

    private static final String DEFAULT_CACHE_PATH = "./repository";
    private final String cachePath;
    private final String mirrorUrl;
    MavenProject project = new MavenProject();
    MavenXpp3Reader reader = new MavenXpp3Reader();

    public MavenTypeDefinition(File path) {
        this(path, DEFAULT_CACHE_PATH, null);
    }

    public MavenTypeDefinition(File path, String cachePath, String mirrorUrl) {
        super(path);
        this.cachePath = cachePath;
        this.mirrorUrl = mirrorUrl;
        initialMavenMirror();
        afterPropertiesSet();
    }

    private void initialMavenMirror() {
        project.setRemoteArtifactRepositories(
                Arrays.asList(
                        new MavenArtifactRepository(
                                "default",
                                StringUtils.defaultString(mirrorUrl, "https://maven.aliyun.com/repository/public"),
                                new DefaultRepositoryLayout(),
                                new ArtifactRepositoryPolicy(),
                                new ArtifactRepositoryPolicy()
                        ))
        );

    }

    @Override
    public void afterPropertiesSet() {
        List<Artifact> cache = new LinkedList<>();
        Model model = null;
        try {
            model = reader.read((Reader) null);
        } catch (Exception ignored) {
        }
        Parent parent = model.getParent();
        for (Dependency dependency : model.getDependencies()) {
//            try {
//                List<Artifact> resolve = new Aether(project, new File(cachePath)).resolve(
//                        new DefaultArtifact(
//                                dependency.getGroupId() + ":" +
//                                        dependency.getArtifactId() + ":" +
//                                        (StringUtils.isNullOrEmpty(dependency.getClassifier()) ?
//                                                StringUtils.isNullOrEmpty(dependency.getVersion()) ? analysis(parent.getVersion(), properties) : analysis(dependency.getVersion(), properties) :
//                                                (dependency.getClassifier() + ":" + analysis(dependency.getVersion(), properties))), properties), StringUtils.defaultString(dependency.getScope(), "runtime")
//                );
//                cache.addAll(resolve);
//            } catch (DependencyResolutionException e) {
//                throw new RuntimeException(e);
//            }

        }
        List<URL> list = new ArrayList<>();
//        try {
//            list.add(original.toURI().toURL());
//        } catch (MalformedURLException ignored) {
//        }
        for (Artifact artifact : cache) {
            File file = artifact.getFile();
            try {
                list.add(file.toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }
    }
}
