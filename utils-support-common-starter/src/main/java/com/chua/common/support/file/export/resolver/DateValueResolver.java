package com.chua.common.support.file.export.resolver;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.date.DateUtils;

import java.util.Date;

/**
 * 日期
 *
 * @author CH
 */
public class DateValueResolver implements ValueResolver {
    private final String format;

    public DateValueResolver(String format) {
        this.format = format;
    }

    @Override
    public Object resolve(Object o) {
        return DateUtils.format(Converter.convertIfNecessary(o, Date.class), format);
    }
}
