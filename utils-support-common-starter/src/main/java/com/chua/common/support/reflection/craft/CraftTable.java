package com.chua.common.support.reflection.craft;

import java.util.function.Consumer;

/**
 * 工艺表
 *
 * @author CH
 */
public interface CraftTable<T> {
    /**
     * 添加描述
     *
     * @param describe 描述
     */
    void addDescribe(T describe);

    /**
     * 是否存在新的描述
     *
     * @return 是否存在新的描述
     */
    boolean hasNewDescribe();

    /**
     * 遍历
     *
     * @param describe 描述
     */
    void forEach(Consumer<T> describe);

    /**
     * 遍历
     *
     * @param describe 描述
     */
    void forCreateEach(Consumer<T> describe);


}
