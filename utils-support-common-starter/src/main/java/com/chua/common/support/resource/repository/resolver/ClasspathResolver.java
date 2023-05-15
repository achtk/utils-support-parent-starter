package com.chua.common.support.resource.repository.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.finder.ClassPathAnyResourceFinder;
import com.chua.common.support.resource.finder.ClassPathResourceFinder;
import com.chua.common.support.resource.finder.ResourceFinder;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.UrlMetadata;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 解释器
 *
 * @author CH
 */
@Spi("classpath")
public final class ClasspathResolver implements Resolver {

    private PathMatcher pathMatcher = PathMatcher.INSTANCE;

    @Override
    public List<Metadata> resolve(URL root, String name) {
        String rootPath = root.toExternalForm();
        ResourceFinder resourceFinder = null;
        if (rootPath.startsWith(CLASSPATH_URL_PREFIX + LEAF_URL_PREFIX)) {
            rootPath = rootPath.replace(CLASSPATH_URL_PREFIX + LEAF_URL_PREFIX, CLASSPATH_URL_ALL_PREFIX);
            resourceFinder = new ClassPathAnyResourceFinder(ResourceConfiguration.DEFAULT);
        } else {
            resourceFinder = new ClassPathResourceFinder(ResourceConfiguration.DEFAULT);
        }
        return resourceFinder.find(rootPath + name)
                .stream().filter(Objects::nonNull).filter(it -> null != it.getUrl()).map(it -> new UrlMetadata(it.getUrl())).collect(Collectors.toList());
    }
}
