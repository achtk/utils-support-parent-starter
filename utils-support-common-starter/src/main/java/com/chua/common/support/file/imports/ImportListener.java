package com.chua.common.support.file.imports;

import java.util.function.Consumer;

/**
 * 导入监听
 *
 * @author CH
 */
public interface ImportListener<T> extends Consumer<T> {
    /**
     * 是否结束
     *
     * @param index 索引位置
     * @return 是否结束
     */
    default boolean isEnd(int index) {
        return false;
    }
}
