package com.chua.common.support.lang.page;

/**
 * 分页数据
 *
 * @author CH
 */
public interface PageData<T> {
    /**
     * 数量
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 结果
     */
    Page<T> find(int page, int pageSize);
}
