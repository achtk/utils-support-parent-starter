package com.chua.common.support.converter.definition;

import com.chua.common.support.lang.date.DateUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * LocalTime
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/26
 */
public class LocalTimeTypeConverter implements TypeConverter<LocalTime> {


    @Override
    public LocalTime convert(Object value) {
        if (null == value) {
            return null;
        }

        if (isAssignableFrom(value, LocalTime.class)) {
            return (LocalTime) value;
        }

        if (isAssignableFrom(value, Date.class)) {
            return DateUtils.toLocalTime((Date) value);
        }

        if (isAssignableFrom(value, LocalDate.class)) {
            return DateUtils.toLocalTime((LocalDate) value);
        }

        if (isAssignableFrom(value, Long.class)) {
            return DateUtils.toLocalTime((Long) value);
        }

        if (isAssignableFrom(value, Calendar.class)) {
            return DateUtils.toLocalTime((Calendar) value);
        }

        if (isAssignableFrom(value, Instant.class)) {
            return DateUtils.toLocalTime((Instant) value);
        }

        if (isAssignableFrom(value, String.class)) {
            try {
                return DateUtils.toLocalTime((String) value);
            } catch (ParseException ignore) {
            }
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<LocalTime> getType() {
        return LocalTime.class;
    }
}
