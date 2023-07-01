package com.chua.common.support.lang.page;

import com.chua.common.support.utils.CollectionUtils;

import java.util.List;

/**
 * 内存数据
 *
 * @author CH
 */
public class PageMemData<T> implements PageData<T> {


    private final List<T> data;

    private PageMemData(List<T> data) {
        this.data = data;
    }

    public synchronized static <T> PageData<T> of(List<T> data) {
        return new PageMemData<>(data);
    }

    @Override
    public Page<T> find(int page, int pageSize) {
        int total = data.size();
        return new Page<T>()
                .setPageNum(page)
                .setPageNum(pageSize)
                .setPages((total / pageSize) + (total % pageSize == 0 ? 0 : 1))
                .setData(CollectionUtils.page(page - 1, pageSize, data))
                .setTotal((long) total);
    }
}
