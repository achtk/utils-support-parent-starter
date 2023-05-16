package com.chua.common.support.lang.depends;

import java.util.List;

/**
 * 解释器
 *
 * @author CH
 */
public interface GrapeResolver {
    /**
     * 生成运行环境
     *
     * @param remote              远程地址
     * @param dependencies        依赖
     * @param dependenciesExclude 去除的依赖
     * @return 环境
     */
    Surroundings resolve(List<ArtifactRepository> remote, List<Dependency> dependencies, List<DependencyExclude> dependenciesExclude);
}
