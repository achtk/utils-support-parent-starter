package com.chua.common.support.lang.page;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页
 *
 * @author CH
 * @since 2022/8/5 15:48
 */
@Data
@Accessors(chain = true)
public class Page<T> {
    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 限制数量
     */
    private Integer pageSize = 10;
    /**
     * 总数
     */
    private Long total = 0L;
    /**
     * 页面数量
     */
    private Integer pages = 1;
    /**
     * 数据
     */
    private List<T> data;
}
