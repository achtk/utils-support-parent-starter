package com.chua.maven.support.depends.definition;

import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.jcabi.aether.Aether;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOLLAR_LEFT_BRACE;
import static com.chua.common.support.constant.NameConstant.POM;
import static com.chua.common.support.constant.NameConstant.POM_SUFFIX;

/**
 * maven定义
 *
 * @author CH
 * @since 2023/09/03
 */
public class MavenTypeDefinition extends ZipTypeDefinition {

    private static final String DEFAULT_CACHE_PATH = "./repository";
    private static final String PROVIDER = "provided";
    private static final String TEST = "test";
    private InputStream pomStream;
    private final String cachePath;
    private final String mirrorUrl;
    MavenProject project = new MavenProject();
    MavenXpp3Reader reader = new MavenXpp3Reader();

    final List<Parent> parents = new LinkedList<>();
    final List<Artifact> cache = new LinkedList<>();
    final Map<String, String> properties = new LinkedHashMap<>();
    final Map<String, Dependency> depends = new LinkedHashMap<>();
    final List<URL> list = new ArrayList<>();

    public MavenTypeDefinition(File path, InputStream pomStream) {
        this(path, pomStream, DEFAULT_CACHE_PATH, null);
    }
    public MavenTypeDefinition(File path) {
        this(path, null, DEFAULT_CACHE_PATH, null);
    }

    public MavenTypeDefinition(File path, String cachePath, String mirrorUrl) {
        this(path, null, cachePath, mirrorUrl);
    }

    public MavenTypeDefinition(File path, InputStream pomStream, String cachePath, String mirrorUrl) {
        super(path);
        this.pomStream = pomStream;
        this.cachePath = cachePath;
        this.mirrorUrl = mirrorUrl;
        initialMavenPomXml();
        initialMavenMirror();
    }

    private void initialMavenPomXml() {
        if(null != pomStream) {
            return;
        }
        Zip zip = new Zip();
        AtomicReference<InputStream> stream = new AtomicReference<>();
        try (InputStream fis = Files.newInputStream(path.toPath())) {
            zip.unFile(fis, fileMedia -> {
                if(fileMedia.getName().endsWith(POM)) {
                    stream.set(fileMedia.getStream());
                    return true;
                }
                return false;
            }, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.pomStream = stream.get();
    }

    private void initialMavenMirror() {
        project.setRemoteArtifactRepositories(
                Collections.singletonList(
                        new MavenArtifactRepository(
                                "default",
                                StringUtils.defaultString(mirrorUrl, "https://maven.aliyun.com/repository/public"),
                                new DefaultRepositoryLayout(),
                                new ArtifactRepositoryPolicy(true, null, null),
                                new ArtifactRepositoryPolicy(true, null, null)
                        ))
        );

    }

    @Override
    public void afterPropertiesSet() {
        Model model = null;
        try(InputStream is = pomStream){
            model = reader.read(is);
        } catch (Exception ignored) {
        }

        if(null == model) {
            return;
        }
        try {
            doAnalysis(model);
        } catch (DependencyResolutionException e) {
            throw new RuntimeException(e);
        }
        for (Artifact artifact : cache) {
            File file = artifact.getFile();
            String name = file.getName();
            if(name.endsWith(POM) || name.endsWith(POM_SUFFIX)) {
                continue;
            }
            try {
                list.add(file.toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }
    }

    /**
     * 解析
     *
     * @param model 模型
     * @throws DependencyResolutionException 依赖项解析异常
     */
    private void doAnalysis(Model model) throws DependencyResolutionException {
        doParentDependence(model);
        List<Dependency> dependencies = model.getDependencies();
        for (Dependency dependency : dependencies) {
            String scope = StringUtils.defaultString(dependency.getScope(), "runtime");
            if(PROVIDER.equalsIgnoreCase(scope) || TEST.equalsIgnoreCase(scope)) {
                continue;
            }
            DefaultArtifact defaultArtifact = new DefaultArtifact(
                    dependency.getGroupId() + ":" +
                            dependency.getArtifactId() + ":" +
                            getVersions(dependency), MapUtils.asStringMap(model.getProperties()));

            try {
                List<org.sonatype.aether.artifact.Artifact> resolve = new Aether(project, new File(cachePath)).resolve(defaultArtifact, scope);
                cache.addAll(resolve);
            } catch (DependencyResolutionException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 获取版本
     *
     * @param dependency 依赖
     * @return {@link String}
     */
    private String getVersions(Dependency dependency) {
        String version = getVersion(dependency);
        if(StringUtils.isNotBlank(version)) {
            return version;
        }

        Dependency dependency1 = depends.get(dependency.getArtifactId());
        if(null != dependency1) {
            return getVersions(dependency1);
        }
        return null;
    }
    /**
     * 获取版本
     *
     * @param dependency 依赖
     * @return {@link String}
     */
    private String getVersion(Dependency dependency) {
        String version1 = MapUtils.getString(properties, dependency.getVersion(), dependency.getVersion());
        if(null != version1 && version1.startsWith(SYMBOL_DOLLAR_LEFT_BRACE)) {
            String name = version1.substring(SYMBOL_DOLLAR_LEFT_BRACE.length(), version1.length() - 1);
            version1 = MapUtils.getString(properties, name, dependency.getVersion());
        }

        if(StringUtils.isEmpty(version1)) {
            version1 = MapUtils.getString(properties, dependency.getArtifactId());
        }

        if(StringUtils.isEmpty(version1)) {
            version1 = MapUtils.getString(properties, dependency.getArtifactId() + ".version");
        }

        if(StringUtils.isNullOrEmpty(dependency.getClassifier())) {
            return version1;
        }
        return dependency.getClassifier() + ":" + version1;
    }

    /**
     * 父依赖
     *
     * @param model   模型
     */
    private void doParentDependence(Model model) {
        Parent parent = model.getParent();
        if(null != parent) {
            parents.add(parent);
            try {
                DefaultArtifact defaultArtifact = new DefaultArtifact(
                        parent.getGroupId() + ":" +
                                parent.getArtifactId() + ":pom:" +
                                MapUtils.getString(properties, parent.getVersion(), parent.getVersion())
                               );
                List<Artifact> pom = new Aether(project, new File(cachePath)).resolve(
                        defaultArtifact, "pom"
                );
                for (Artifact artifact : pom) {
                    cache.add(artifact);
                    File file = artifact.getFile();
                    if(null != file) {
                        doParentDependence(file);
                    }
                }
            } catch (DependencyResolutionException e) {
                e.printStackTrace();
            }
            properties.put("${project.version}", parent.getVersion());
        }
        if(StringUtils.isNotBlank(model.getVersion())) {
            properties.put("${project.version}", model.getVersion());
        }
    }

    /**
     * 父依赖
     *
     * @param file    文件
     */
    private void doParentDependence(File file) {
        if(!file.getName().endsWith(POM_SUFFIX)) {
            return;
        }
        Model model = null;
        try(InputStream is = Files.newInputStream(file.toPath())){
            model = reader.read(is);
        } catch (Exception ignored) {
        }

        if(null == model) {
            return;
        }
        try {
            doAnalysis(model);
            doAnalysis(model.getProperties());
            doAnalysis(model.getDependencyManagement());
        } catch (DependencyResolutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析
     *
     * @param properties 特性
     */
    private void doAnalysis(Properties properties) {
        this.properties.putAll(MapUtils.asStringMap(properties));
    }

    /**
     * 解析
     *
     * @param dependencyManagement 依赖关系管理
     */
    private void doAnalysis(DependencyManagement dependencyManagement) {
        if(null == dependencyManagement) {
            return;
        }
        List<Dependency> dependencies = dependencyManagement.getDependencies();
        for (Dependency dependency : dependencies) {
            depends.put(dependency.getArtifactId(), dependency);
        }
    }

    @Override
    public List<java.net.URL> getDepends() {
        return list;
    }
}
