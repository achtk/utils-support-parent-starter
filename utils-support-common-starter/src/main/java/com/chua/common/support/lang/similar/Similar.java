package com.chua.common.support.lang.similar;

import java.io.File;
import java.net.URL;

/**
 * 相似性
 *
 * @author CH
 */
public interface Similar {
    /**
     * 环境
     *
     * @param key   索引
     * @param value 值
     * @return this
     * @throws Exception ex
     */
    Similar environment(String key, Object value) throws Exception;

    /**
     * 相似性
     *
     * @param source 来源
     * @param target 目标
     * @return 相似性
     * @throws Exception ex
     */
    double match(String source, String target) throws Exception;

    /**
     * 相似性
     *
     * @param source 来源
     * @param target 目标
     * @return 相似性
     * @throws Exception ex
     */
    double match(URL source, URL target) throws Exception;

    /**
     * 相似性
     *
     * @param source 来源
     * @param target 目标
     * @return 相似性
     * @throws Exception ex
     */
    default double match(File source, File target) throws Exception {
        return match(source.getAbsolutePath(), target.getAbsolutePath());
    }
}
