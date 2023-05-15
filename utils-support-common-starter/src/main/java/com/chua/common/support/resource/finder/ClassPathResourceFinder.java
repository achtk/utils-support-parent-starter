package com.chua.common.support.resource.finder;

import com.chua.common.support.lang.Cost;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipFile;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 系统资源查找器
 * @author CH
 */
public class ClassPathResourceFinder extends AbstractResourceFinder{

    public ClassPathResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        name = CLASSPATH_URL_PREFIX + name;
        if (getPathMatcher(name.substring(CLASSPATH_URL_PREFIX.length()))) {
            return findPathMatchingResources(name, excludes.toArray(EMPTY_ARRAY));
        }
        return findAllClassPathResources(name);
    }

    /**
     * 获取结果集
     *
     * @param name     名称
     * @param excludes 除外文件
     * @return 结果集
     */
    public Set<Resource> getResources(String name, String[] excludes) {
        //是否存在路径通配符
        if (getPathMatcher(name.substring(CLASSPATH_URL_PREFIX.length()))) {
            return findPathMatchingResources(name, excludes);
        }
        return findAllClassPathResources(name);
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
        String rootPath = classPathRoot.substring(CLASSPATH_URL_PREFIX.length()).trim();
        //处理根目录
        rootPath = StringUtils.trimIfStartWith(rootPath, SYMBOL_LEFT_SLASH);
        //待匹配的文件
        String subPath = name.substring(classPathRoot.length());
        //获取目录文件
        Set<Resource> resources = getResources(CLASSPATH_URL_PREFIX.concat(rootPath), excludes);
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

        for (Resource resource : resources) {
            if (null == resource) {
                return;
            }
            URL url = null;
            try {
                url = resource.getUrl();
            } catch (Exception ignored) {
                return;
            }

            if (null == url) {
                return;
            }
            //如果是jar, war, zip, wsjar, vfszip文件
            Cost sub = Cost.trace("处理目录: {}\r\n");
            try {
                if (UrlUtils.isAllJar(url)) {
                    try {
                        doFindPathMatchingJarResources(url, subPath, result);
                    } catch (IOException ignored) {
                    }
                } else {
                    doFindPathMatchingResources(subPath, new File(url.getFile()), result);
                }
            } finally {
                sub.console(url);
            }
        }
        //匹配的数量
        int matchSize = result.size();
        long time = System.currentTimeMillis() - startTime;
        parent.console(name, matchSize, size, matchSize * 1000 / time);
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
     * @param matcherPath 文件
     * @param result      结果集
     */
    private void doFindPathMatchingResources(String matcherPath, File file, Set<Resource> result) {
        String fullPattern = StringUtils.replace(file.getAbsolutePath() + "/" + matcherPath, File.separator, "/");
        File[] list = file.listFiles();
        if (null == list) {
            return;
        }
        for (File content : list) {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && pathMatcher.matchStart(fullPattern, currPath + "/")) {
                if (content.canRead()) {
                    doFindPathMatchingResources(matcherPath, content, result);
                }
                continue;
            }

            if (pathMatcher.match(fullPattern, currPath)) {
                result.add(Resource.create(content));
            }
        }
    }


    /**
     * 解析文件夹下的文件
     *
     * @param name 文件夹
     * @return 资源
     */
    private Set<Resource> findAllClassPathResources(String name) {
        //获取路径
        String path = name.substring(CLASSPATH_URL_PREFIX.length()).trim();
        return Collections.singleton(Resource.create(classLoader.getResource(path)));
    }

}
