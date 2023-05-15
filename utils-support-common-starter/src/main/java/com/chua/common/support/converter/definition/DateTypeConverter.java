package com.chua.common.support.converter.definition;


import com.chua.common.support.date.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 时间格式转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/26
 */
public class DateTypeConverter implements TypeConverter<Date> {

    @Override
    public Date convert(Object value) {
        Date date = convertIfNecessary(value);
        if (null != date) {
            return date;
        }
        //尝试字符串转化
        String string = value.toString();
        try {
            return DateUtils.parseDate(string);
        } catch (ParseException ignored) {
        }
        return null;
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }
}
