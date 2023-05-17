package com.chua.common.support.file;

import java.util.List;

/**
 * 对象文件
 *
 * @author CH
 */
public interface ObjectFile {
    /**
     * 解析
     *
     * @param target 目标类型
     * @param <E>    类型
     * @return 对象
     */
    <E> E parse(Class<E> target);

    /**
     * 解析
     *
     * @param target 目标类型
     * @param <E>    类型
     * @return 对象
     */
    <E> List<E> parseArray(Class<E> target);
}
