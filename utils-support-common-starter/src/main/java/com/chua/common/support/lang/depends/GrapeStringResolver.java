package com.chua.common.support.lang.depends;

/**
 * 解释器
 *
 * @author CH
 */
public interface GrapeStringResolver {
    /**
     * 生成运行环境
     *
     * @param args              依赖
     * @param cachePat cache
     * @param parentClassLoader classloader
     * @return 环境
     */
    Surroundings resolve(String[] args, String cachePat, ClassLoader parentClassLoader);
}
