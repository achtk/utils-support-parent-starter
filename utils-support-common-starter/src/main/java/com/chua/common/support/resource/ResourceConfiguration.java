package com.chua.common.support.resource;

import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.resource.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;
import java.util.function.Consumer;

/**
 * 资源配置
 * @author CH
 */
@Builder
@Data
public class ResourceConfiguration {
    public static final ResourceConfiguration DEFAULT = ResourceConfiguration.builder().build();

    @Builder.Default
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Builder.Default
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Singular("excludes")
    private Set<String> excludes;
    /**
     * 是否并行
     */
    @Builder.Default
    private boolean isParallel = true;
    /**
     * 监听
     */
    private Consumer<Resource> consumer;
    /**
     * 初始化
     * @param name 初始化
     * @return 结果
     */
    public ResourceProvider create(String name) {
        return ResourceProvider.of(name, this);
    }
}
