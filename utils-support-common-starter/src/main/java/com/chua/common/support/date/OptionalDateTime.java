package com.chua.common.support.date;


import java.time.LocalDateTime;


/**
 * 可读日期时间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public interface OptionalDateTime {
    /**
     * 获取{@link DateTime}LocalDateTime
     *
     * @return LocalDateTime
     */
    LocalDateTime toLocalDateTime();

    /**
     * 闰年
     *
     * @return 闰年
     */
    default boolean isLeap() {
        LocalDateTime localDateTime = toLocalDateTime();
        int year = localDateTime.getYear();
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

}
