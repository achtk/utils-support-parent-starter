package com.chua.common.support.resource.repository;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * 资源仓库
 *
 * @author CH
 */
public interface Repository {
    /**
     * 初始化
     *
     * @param hasLeaf 是否深度检索
     * @return 目录
     */
    static Repository classpath(boolean hasLeaf) {
        return hasLeaf ? of("classpath:leaf:") : classpath();
    }

    /**
     * 初始化
     *
     * @return 目录
     */
    static Repository classpath() {
        return of("classpath:");
    }

    /**
     * 初始化
     *
     * @param name 环境变量
     * @return 目录
     */
    static Repository system(String name) {
        return of(System.getProperty(name));
    }

    /**
     * 初始化
     *
     * @return 目录
     */
    static Repository current() {
        return of(".");
    }

    /**
     * user home
     * @return user home
     */
    static Repository userHome() {
        return of(Projects.userHome());
    }

    /**
     * 合并资源
     *
     * @param repository 资源
     * @return 目录
     */
    Repository add(Repository repository);

    /**
     * 初始化
     *
     * @param url 资源
     * @return 目录
     */
    static Repository of(URL... url) {
        return new VfsRepository(url);
    }

    /**
     * 获取检索资源目录
     *
     * @return 目录
     */
    URL[] getParent();

    /**
     * 初始化
     *
     * @param url 资源
     * @return 目录
     */
    @SneakyThrows
    static Repository of(File url) {
        return new VfsRepository(url.toURI().toURL());
    }

    /**
     * 初始化
     *
     * @param url 资源
     * @return 目录
     */
    @SneakyThrows
    public static Repository of(String... url) {
        if (null == url) {
            return new VfsRepository();
        }

        URL[] urls = new URL[url.length];
        for (int i = 0; i < url.length; i++) {
            urls[i] = null;

            String s = url[i];
            if (null == s) {
                continue;
            }

            if (s.startsWith(HTTP)) {
                urls[i] = new URL(s);
                continue;
            }

            File temp = new File(s);
            if (!temp.exists() && !s.contains(":")) {
                temp.mkdirs();
            }

            if (temp.exists()) {
                urls[i] = temp.toURI().toURL();
                continue;
            }

            doAnalysisUrl(s, urls, i);
        }

        return new VfsRepository(urls);
    }

    /**
     * 解析url
     *
     * @param s    url
     * @param urls urls
     * @param i    i
     */
    static void doAnalysisUrl(String s, URL[] urls, int i) {
        ServiceProvider<URLStreamHandler> provider = ServiceProvider.of(URLStreamHandler.class);
        for (URLStreamHandler streamHandler : provider.collect()) {
            try {
                URL url1 = new URL(null, s, streamHandler);
                urls[i] = url1;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 查找文件
     *
     * @return 元数据
     */
    default Metadata first() {
        return first("");
    }

    /**
     * 查找文件
     *
     * @param path 路径
     * @return 元数据
     */
    default Metadata first(String path) {
        List<Metadata> metadata = getMetadata(path);
        return CollectionUtils.isEmpty(metadata) ? null : metadata.get(0);
    }

    /**
     * 查找文件
     *
     * @param path 路径
     * @return 元数据
     */
    List<Metadata> getMetadata(String path);

    /**
     * 查找文件
     *
     * @return 元数据
     */
    default List<Metadata> getMetadata() {
        return getMetadata("");
    }

    /**
     * 查找文件
     *
     * @param path 路径
     * @return 元数据
     */
    default Repository resolve(String path) {
        return Repository.of(getMetadata(path).stream().map(Metadata::toUrl).filter(Objects::nonNull).toArray(URL[]::new));
    }

    /**
     * 写入文件
     *
     * @param property 目录
     * @param isOver   是否覆盖
     */
    default void transferTo(String property, boolean isOver) {
        List<Metadata> metadata = this.getMetadata();
        String[] folder = property.split(";");
        if (!isOver) {
            metadata = checkExist(folder, metadata);
        }

        for (Metadata metadatum : metadata) {
            inject(metadatum, folder);
        }
    }

    /**
     * 安装资源
     *
     * @param metadata 资源
     * @param property 目录
     */
    default void inject(Metadata metadata, String[] property) {
        if(metadata.isCompressFile()) {
            metadata.unTransferTo(property);
            return;
        }

        metadata.transferTo(property);
    }

    /**
     * 资源是否已存在
     *
     * @param metadata 资源
     * @param folder   目录
     * @return 资源是否已存在
     */
    default List<Metadata> checkExist(String[] folder, List<Metadata> metadata) {
        List<Metadata> rs = new LinkedList<>();
        for (Metadata metadatum : metadata) {
            if (exist(metadatum, folder)) {
                continue;
            }
            rs.add(metadatum);
        }

        return rs;
    }

    /**
     * 资源是否已存在
     *
     * @param metadata 资源
     * @param folder   目录
     * @return 资源是否已存在
     */
    default boolean exist(Metadata metadata, String[] folder) {
        String name = metadata.getName();
        for (String s : folder) {
            if (new File(s, name).exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    default boolean isEmpty() {
        return getMetadata().isEmpty();
    }

    /**
     * 远程路径资源
     * @param url 地址
     */
    Repository remoteResource(String url);
}
