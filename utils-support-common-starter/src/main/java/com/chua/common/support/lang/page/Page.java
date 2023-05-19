package com.chua.common.support.lang.page;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页
 *
 * @author CH
 * @since 2022/8/5 15:48
 */
@Data
@Builder
public class Page<T> {
    /**
     * 当前页
     */
    private int current;

    /**
     * 限制数量
     */
    private int size;
    /**
     * 总数
     */
    private long total;
    /**
     * 页面数量
     */
    private int pages;
    /**
     * 数据
     */
    private List<T> data;
}
