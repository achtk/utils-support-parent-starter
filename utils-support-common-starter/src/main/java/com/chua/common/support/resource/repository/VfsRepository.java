package com.chua.common.support.resource.repository;

import com.chua.common.support.context.environment.StandardEnvironment;
import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.lang.download.DownloadHandler;
import com.chua.common.support.lang.download.Downloader;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.repository.resolver.Resolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.FILE;
import static com.chua.common.support.constant.CommonConstant.URI;

/**
 * 资源仓库
 *
 * @author CH
 */
public class VfsRepository implements Repository {

    private final ConfigurableBeanFactory beanFactory = ApplicationContextBuilder.newBuilder()
            .environment(new StandardEnvironment())
            .openScanner(false)
            .build();

    protected URL[] url;
    private String removeUrl;

    protected VfsRepository(URL... url) {
        this.url = url;
        beanFactory.registerBean(this);
        beanFactory.registerBean(PathMatcher.INSTANCE);
    }

    @Override
    public Repository add(Repository repository) {
        URL[] urls = ArrayUtils.addAll(url, repository.getParent());
        return Repository.of(urls);
    }

    @Override
    public URL[] getParent() {
        return url;
    }

    /**
     * 查找文件
     *
     * @param path 路径
     */
    @Override
    public List<Metadata> getMetadata(String path) {
        if(StringUtils.isNullOrEmpty(path)) {
            return Arrays.stream(url).map(UrlMetadata::new).collect(Collectors.toList());
        }

        List<Metadata> metadata = new LinkedList<>();
        for (URL url1 : url) {
            if (null == url1) {
                continue;
            }

            if (FileUtils.exist(path)) {
                metadata.add(new FileSystemMetadata(path));
                continue;
            }

            Resolver resolver = ServiceProvider.of(Resolver.class).getNewExtension(url1.getProtocol());
            if (null == resolver) {
                continue;
            }

            beanFactory.autowire(resolver);

            metadata.addAll(resolver.resolve(url1, path));
        }

        if(metadata.isEmpty() && StringUtils.isNotEmpty(removeUrl)) {
            downloadToLocal(metadata);
        }
        return metadata;
    }

    /**
     * 下载到本地
     * @param metadata 结果集
     */
    private void downloadToLocal(List<Metadata> metadata) {
        if(checkCacheExist(metadata)) {
            return;
        }
        File temp = getFile();
        if(null == temp) {
            return;
        }
        try {
            Downloader downloader = Downloader.newBuilder()
                    .buffer(10 * 1024 * 1024).savePath(temp.getAbsolutePath())
                    .build();
            downloader.download(removeUrl);
            metadata.add(new FileSystemMetadata(new File(temp, downloader.getFileName())).setEqualsOrigin(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkCacheExist(List<Metadata> metadata) {
        try {
            URL url1 = new URL(removeUrl);
            String fileName = UrlUtils.getFileName(url1.openConnection());
            if(null != fileName) {
                File file = getFile();
                if(null != file) {
                    file = new File(file, fileName);
                }
                if(null != file && file.exists()) {
                    metadata.add(new FileSystemMetadata(file).setEqualsOrigin(false));
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * 获取目录
     * @return 目录
     */
    private File getFile() {
        for (URL url1 : url) {
            if(FILE.equals(url1.getProtocol())) {
                return new File(url1.getFile());
            }
        }
        return null;
    }

    @Override
    public Repository remoteResource(String url) {
        this.removeUrl = url;
        return this;
    }

}
