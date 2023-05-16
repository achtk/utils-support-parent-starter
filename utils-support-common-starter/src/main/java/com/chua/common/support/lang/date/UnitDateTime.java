package com.chua.common.support.lang.date;


import java.time.LocalDateTime;


/**
 * 可读日期时间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public interface UnitDateTime {
    /**
     * 获取{@link DateTime}LocalDateTime
     *
     * @return LocalDateTime
     */
    LocalDateTime toLocalDateTime();


}
