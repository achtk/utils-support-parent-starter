package com.chua.common.support.lang.depends;

import java.io.File;
import java.io.InputStream;

/**
 * 解释器
 *
 * @author CH
 */
public interface GrapeFileResolver {
    /**
     * 生成运行环境
     *
     * @param stream 依赖
     * @param parent
     * @param file
     * @return 环境
     */
    Surroundings resolve(InputStream stream, String cachePat, ClassLoader parent, File file);
}
