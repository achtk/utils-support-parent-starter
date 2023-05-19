package com.chua.common.support.lang.page;

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
        return Page.<T>builder().data(data).build();
    }
}
