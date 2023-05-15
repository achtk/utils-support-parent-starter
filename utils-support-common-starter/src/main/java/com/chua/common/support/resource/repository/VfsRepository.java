package com.chua.common.support.resource.repository;

import com.chua.common.support.context.environment.StandardEnvironment;
import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.repository.resolver.Resolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.FileUtils;
import com.google.common.base.Strings;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源仓库
 *
 * @author CH
 */
public class VfsRepository implements Repository {

    private final ConfigurableBeanFactory beanFactory = ApplicationContextBuilder.newBuilder()
            .environment(new StandardEnvironment())
            .build();

    protected URL[] url;

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
        if(Strings.isNullOrEmpty(path)) {
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

            Resolver resolver = ServiceProvider.of(Resolver.class).getExtension(url1.getProtocol());
            if (null == resolver) {
                continue;
            }

            beanFactory.autowire(resolver);

            metadata.addAll(resolver.resolve(url1, path));
        }

        return metadata;
    }

}
