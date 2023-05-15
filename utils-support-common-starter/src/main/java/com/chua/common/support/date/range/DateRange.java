package com.chua.common.support.date.range;

import com.chua.common.support.range.Range;

import java.time.LocalDateTime;

/**
 * 时间区间
 *
 * @author CH
 */
public interface DateRange {
    /**
     * 区间
     *
     * @return 区间
     */
    Range<LocalDateTime> range();

    /**
     * 是否包含
     *
     * @param dateTime 时间
     * @return 结果
     */
    default boolean contains(LocalDateTime dateTime) {
        return range().contains(dateTime);
    }

}
