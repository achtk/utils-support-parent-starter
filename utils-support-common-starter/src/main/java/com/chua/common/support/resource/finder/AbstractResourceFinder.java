package com.chua.common.support.resource.finder;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 资源查找器
 *
 * @author CH
 */
public abstract class AbstractResourceFinder implements ResourceFinder{

    protected final PathMatcher pathMatcher;
    protected ResourceConfiguration configuration;
    protected Set<String> excludes;

    protected ClassLoader classLoader;

    protected Consumer<Resource> consumer;

    public AbstractResourceFinder(ResourceConfiguration configuration) {
        this.configuration = configuration;
        this.pathMatcher = configuration.getPathMatcher();
        this.excludes =  configuration.getExcludes();
        this.classLoader = configuration.getClassLoader();
        this.consumer = Optional.ofNullable(configuration.getConsumer()).orElse((SafeConsumer<Resource>) resource -> {
            
        });
    }


    /**
     * 是否匹配
     *
     * @param path 路径
     * @return 匹配成功返回true
     */
    protected boolean getPathMatcher(String path) {
        return pathMatcher.isPattern(path);
    }


    /**
     * 获取真实名称
     * @param filePath 文件路径
     * @param rootPath 根目录
     * @return 获取真实名称
     */
    protected String getRealName(String filePath, String rootPath) {
        if(StringUtils.isNullOrEmpty(rootPath)) {
            return filePath.replace("\\", "/");
        }

        rootPath = rootPath.replace("\\", "/");
        filePath = filePath.replace("\\", "/");
        return filePath.substring(rootPath.length()).replace("//", "/");
    }

    /**
     * 是否匹配
     * @param name 文件名称 + 路径
     * @param matchPath 匹配路径
     * @return 是否匹配
     */
    protected boolean isMatch(String name, String matchPath) {
        if(SYMBOL_ASTERISK.equals(matchPath) || SYMBOL_ASTERISK_ANY.equals(matchPath)) {
            return true;
        }

        if(matchPath.contains(SYMBOL_ASTERISK) || matchPath.contains(SYMBOL_QUESTION)) {
            return pathMatcher.match(matchPath, name);
        }

        return name.contains(matchPath);
    }

    /**
     * 是否除外
     * @param absolutePath 路径
     * @return 结果
     */
    protected boolean isExclude(String absolutePath) {
        for (String exclude : excludes) {
            if(isMatch(absolutePath, exclude)) {
                return true;
            }
        }

        return false;
    }
    /**
     * 获取根目录
     *
     * @param name 记录
     * @return 根目录
     */
    protected String findPathRootPath(String name) {
        int prefixEnd = name.indexOf(':') + 1;
        int rootDirEnd = name.length();
        while (rootDirEnd > prefixEnd && getPathMatcher(name.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = name.lastIndexOf(SYMBOL_LEFT_SLASH_CHAR, rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }

        return name.substring(0, rootDirEnd);
    }

    /**
     * 获取路径
     * @param path 路径
     * @return 路径
     */
    public String getFullPath(String path) {
        path = path.replace("\\", SYMBOL_LEFT_SLASH);
        List<String> sep = new LinkedList<>();
        for (String item : path.split(SYMBOL_LEFT_SLASH)) {
            if(item.contains("*") || item.contains(SYMBOL_QUESTION)) {
                break;
            }

            sep.add(item);
        }

        return Joiner.on("/").join(sep);
    }


    /**
     * 获取路径
     * @param path 路径
     * @return 路径
     */
    public String getMatchPath(String path) {
        path = path.replace("\\", SYMBOL_LEFT_SLASH);
        List<String> sep = new LinkedList<>();
        for (String item : path.split(SYMBOL_LEFT_SLASH)) {
            if(item.contains("*") || item.contains("?")) {
                sep.add(item);
            }
        }

        return Joiner.on("/").join(sep);
    }


    /**
     * 获取文件名称
     *
     * @param urlForm url地址
     * @return 文件名称
     */
    protected String getUrlName(String urlForm) {
        int index = urlForm.indexOf(JAR_URL_SEPARATOR);
        if (index == -1) {
            return urlForm;
        }
        String substring = urlForm.substring(0, index);
        return FileUtils.getName(substring);
    }
}