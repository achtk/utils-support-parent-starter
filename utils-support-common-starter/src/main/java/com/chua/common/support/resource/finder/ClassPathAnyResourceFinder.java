package com.chua.common.support.resource.finder;

import com.chua.common.support.lang.Cost;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipFile;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 系统资源查找器
 *
 * @author CH
 */
@Slf4j
public class ClassPathAnyResourceFinder extends AbstractResourceFinder {

    public ClassPathAnyResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        return analysisAnyResources(CLASSPATH_URL_ALL_PREFIX + name, excludes.toArray(EMPTY_ARRAY));
    }

    /**
     * 分析所有文件
     *
     * @param name     查询资源
     * @param excludes 除外资源
     * @return resources
     */
    private Set<Resource> analysisAnyResources(String name, String[] excludes) {
        //是否存在路径通配符
        if (getPathMatcher(name.substring(CLASSPATH_URL_ALL_PREFIX.length()))) {
            return findPathMatchingResources(name, excludes);
        }
        return findAllClassPathResources(name, excludes);
    }

    /**
     * 查询路径匹配资源
     *
     * @param name     待查询的表达式
     * @param excludes 除外文件
     * @return 结果集
     */
    public Set<Resource> findPathMatchingResources(String name, String[] excludes) {
        Set<Resource> result = new LinkedHashSet<>();
        //获取根目录
        String classPathRoot = findPathRootPath(name);
        //查询根目录
        String rootPath = classPathRoot.substring(CLASSPATH_URL_ALL_PREFIX.length()).trim();
        //处理根目录
        rootPath = StringUtils.trimIfStartWith(rootPath, SYMBOL_LEFT_SLASH);
        //待匹配的文件
        String subPath = name.substring(classPathRoot.length());
        //获取目录文件
        Set<Resource> resources = analysisAnyResources(CLASSPATH_URL_ALL_PREFIX.concat(rootPath), excludes);
        //解析资源
        this.analysisResources(resources, name, subPath, result);

        return result;
    }

    /**
     * 解析数据
     *
     * @param resources 待查询的父目录
     * @param name      查询文件
     * @param subPath   子目录
     * @param result    结果集
     */
    private void analysisResources(Set<Resource> resources, String name, String subPath, Set<Resource> result) {
        Cost parent = Cost.debug("表达式: {}, [{}/{}] 总共被检索, 扫描{}个URL. 平均: {}/s");
        long startTime = System.currentTimeMillis();
        int size = resources.size();

        if(resources.isEmpty()) {
            return;
        }

        (configuration.isParallel() ? resources.stream().parallel() : resources.stream()).filter(Objects::nonNull).filter(it -> Resource.class.isAssignableFrom(it.getClass())).forEach(resource -> {
            URL url = null;
            try {
                url = resource.getUrl();
            } catch (Exception ignored) {
                return;
            }
            //如果是jar, war, zip, wsjar, vfszip文件
            Cost debug = Cost.trace("处理目录: {}\r\n");
            try {
                if (UrlUtils.isAllJar(url)) {
                    try {
                        doFindPathMatchingJarResources(url, subPath, result);
                    } catch (IOException ignored) {
                    }
                } else {
                    doFindPathMatchingResources(url, subPath, result);
                }
            } finally {
                debug.console(url);
            }
        });
        //匹配的数量
        int matchSize = result.size();
        long time = System.currentTimeMillis() - startTime;
        parent.console(name, matchSize, matchSize, size, matchSize * 1000 / time);
    }

    /**
     * 查找Jar中的文件
     *
     * @param url     jarURL
     * @param subPath 文件
     * @param result  结果集
     */
    private void doFindPathMatchingJarResources(URL url, String subPath, Set<Resource> result) throws IOException {
        ZipFile jarFile = null;
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                urlConnection.setUseCaches(false);
                jarFile = ((JarURLConnection) urlConnection).getJarFile();
            }
        } catch (Throwable ignore) {
        }

        if (null == jarFile) {
            return;
        }
        String externalForm = url.toExternalForm();
        int index = externalForm.indexOf(JAR_URL_SEPARATOR);
        String jarExternalForm = externalForm.substring(0, index) + JAR_URL_SEPARATOR;
        String prefix = externalForm.substring(index + 2);
        int index1 = externalForm.indexOf(SYMBOL_ASTERISK);
        if (index1 > -1) {
            prefix = externalForm.substring(0, index1);
        }
        String newPrefix = prefix;

        int length = newPrefix.length();
        try (ZipFile closeJarFile = jarFile) {
            closeJarFile.stream().forEach(jarEntry -> {
                String jarEntryName = jarEntry.getName();
                if (!jarEntryName.startsWith(newPrefix)) {
                    return;
                }
                String newJarEntryName = jarEntryName.substring(length);
                String newUrl = jarExternalForm + jarEntryName;
                if (SYMBOL_ASTERISK.equals(newJarEntryName) || pathMatcher.match(subPath, newJarEntryName)) {
                    Resource resource = Resource.create(newUrl);
                    consumer.accept(resource);
                    result.add(resource);
                }
            });
        }
    }

    /**
     * 查询file:下的文件
     *
     * @param url         fileURL
     * @param matcherPath 文件
     * @param result      结果集
     */
    private void doFindPathMatchingResources(URL url, String matcherPath, Set<Resource> result) {
        doFindPathMatchingLocalResources(url.getFile(), new File(url.getFile()).getAbsolutePath(), matcherPath, result);
    }

    /**
     * 解析文件夹下的文件
     *
     * @param path        文件夹
     * @param root        更目录
     * @param matcherPath 待匹配路径
     * @param result      结果集
     */
    private void doFindPathMatchingLocalResources(String path, String root, String matcherPath, Set<Resource> result) {
        File file = new File(path);
        if (file.isFile()) {
            return;
        }

        Path path1 = Paths.get(file.getAbsolutePath());
        try {
            Files.walkFileTree(path1, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        String absolutePath = file.toString();
                        int length = absolutePath.length();
                        int length1 = root.length();
                        if (length < length1 + 1) {
                            return FileVisitResult.CONTINUE;
                        }
                        String embeddedSubPath = absolutePath.substring(root.length() + 1);
                        if (embeddedSubPath.startsWith(SYMBOL_LEFT_SLASH)) {
                            embeddedSubPath = embeddedSubPath.substring(1);
                        }

                        embeddedSubPath = embeddedSubPath.replace(SYMBOL_RIGHT_SLASH, SYMBOL_LEFT_SLASH);

                        if ((SYMBOL_ASTERISK.equals(matcherPath) || pathMatcher.match(matcherPath, embeddedSubPath))) {
                            Resource resource = Resource.create(file.toFile());
                            consumer.accept(resource);
                            result.add(resource);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
        }
    }


    /**
     * 解析文件夹下的文件
     *
     * @param name     文件夹
     * @param excludes 除外文件
     * @return 资源
     */
    private Set<Resource> findAllClassPathResources(String name, String[] excludes) {
        Set<Resource> result = new LinkedHashSet<>();
        //扩展
        Set<String> additionalCollections = new HashSet<>();
        //获取路径
        String path = name.substring(CLASSPATH_URL_ALL_PREFIX.length()).trim();

        if (path.startsWith(SYMBOL_LEFT_SLASH)) {
            path = path.substring(1);
        }
        //获取资源文件
        Enumeration<URL> enumeration = null;

        try {
            enumeration = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != enumeration) {
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                if (ignoreJar(excludes, getUrlName(url.toExternalForm()))) {
                    continue;
                }
                additionalCollections.add(url.toExternalForm());
                result.add(Resource.create(url));
            }
        }


        if ("".equals(path)) {
            //扫描根目录Jar文件
            addAllClassLoaderJarRoots(classLoader, result, additionalCollections, excludes);
        }
        return result;
    }


    /**
     * 扫描根目录的 Jar文件
     *
     * @param classLoader           类加载器
     * @param result                结果集
     * @param additionalCollections 扩展Jar集合
     * @param excludes              除外文件
     */
    private void addAllClassLoaderJarRoots(ClassLoader
                                                   classLoader, Set<Resource> result, Set<String> additionalCollections, String[] excludes) {
        if (classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            try {
                Arrays.stream(urls).parallel().forEach(url -> {
                    if (ignoreJar(excludes, getUrlName(url.toExternalForm()))) {
                        return;
                    }

                    Resource resource;
                    if (JAR.equals(url.getProtocol())) {
                        resource = Resource.create(url);
                    } else {
                        resource = Resource.create(JAR_URL_PREFIX + url + JAR_URL_SEPARATOR);
                    }

                    if (additionalCollections.contains(resource.getUrl().toExternalForm())) {
                        return;
                    }
                    additionalCollections.add(resource.getUrl().toExternalForm());
                    result.add(resource);
                });
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files since ClassLoader [" + classLoader + "] does not support 'getURLs()': " + ex);
                }
            }
        }
        //是系统加载器扫描 清单条目
        if (classLoader == ClassLoader.getSystemClassLoader()) {
            addClassPathManifestEntries(result, additionalCollections, excludes);
        }

        if (classLoader != null) {
            try {
                addAllClassLoaderJarRoots(classLoader.getParent(), result, additionalCollections, excludes);
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader + "] does not support 'getParent()': " + ex);
                }
            }
        }
    }

    /**
     * 扫描清单条目
     *
     * @param result                结果集
     * @param additionalCollections 扩展Jar集合
     * @param excludes              除外文件
     */
    public void addClassPathManifestEntries(Set<Resource> result, Set<String> additionalCollections, String[]
            excludes) {
        try {
            String javaClassPathProperty = System.getProperty(JAVA_CLASS_PATH);
            for (String path : StringUtils.delimitedListToStringArray(javaClassPathProperty, System.getProperty(PATH_SEPARATOR))) {
                String filePath = new File(path).getAbsolutePath();
                int prefixIndex = filePath.indexOf(SYMBOL_COLON_CHAR);
                if (prefixIndex == 1) {
                    filePath = StringUtils.capitalize(filePath);
                }

                if (ignoreJar(excludes, FileUtils.getName(filePath))) {
                    continue;
                }

                if (!UrlUtils.isAllJar(filePath)) {
                    continue;
                }

                Resource resource = Resource.create(JAR_URL_PREFIX + FILE_URL_PREFIX + SYMBOL_LEFT_SLASH + filePath.replace("\\", SYMBOL_LEFT_SLASH) + JAR_URL_SEPARATOR);

                if (additionalCollections.contains(resource.getUrl().toExternalForm())) {
                    continue;
                }

                additionalCollections.add(resource.getUrl().toExternalForm());
                result.add(resource);
            }
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex);
            }
        }
    }


    /**
     * 忽略文件
     *
     * @param excludes 忽略列表
     * @param name     当前文件
     * @return 是否是除外的文件
     */
    protected boolean ignoreJar(String[] excludes, String name) {
        if (null == excludes || excludes.length == 0 || StringUtils.isEmpty(name)) {
            return false;
        }
        for (String exclude : excludes) {
            if (pathMatcher.match(exclude, name)) {
                return true;
            }
        }
        return false;
    }
}
