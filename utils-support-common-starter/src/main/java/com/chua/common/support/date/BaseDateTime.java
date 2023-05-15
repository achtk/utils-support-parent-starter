package com.chua.common.support.date;

import com.chua.common.support.date.format.DateTimeFormat;
import com.chua.common.support.date.format.SimpleDateTimeFormat;

import java.io.Serializable;

/**
 * 基础时间处理类
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public abstract class BaseDateTime implements ReadableDateTime, ReadableDateRange, Serializable {
    /**
     * 时间格式解析器
     */
    protected static final DateTimeFormat DEFAULT_FORMAT = new SimpleDateTimeFormat();

}
