package com.chua.common.support.converter.definition;

import com.chua.common.support.date.DateUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * localDate
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/26
 */
public class LocalDateTypeConverter implements TypeConverter<LocalDate> {
    @Override
    public LocalDate convert(Object value) {
        if (null == value) {
            return null;
        }

        if (isAssignableFrom(value, LocalDateTime.class)) {
            return (LocalDate) value;
        }

        if (isAssignableFrom(value, Date.class)) {
            return DateUtils.toLocalDate((Date) value);
        }

        if (isAssignableFrom(value, LocalTime.class)) {
            return DateUtils.toLocalDate((LocalTime) value);
        }

        if (isAssignableFrom(value, Long.class)) {
            return DateUtils.toLocalDate((Long) value);
        }

        if (isAssignableFrom(value, Calendar.class)) {
            return DateUtils.toLocalDate((Calendar) value);
        }

        if (isAssignableFrom(value, Instant.class)) {
            return DateUtils.toLocalDate((Instant) value);
        }

        if (isAssignableFrom(value, String.class)) {
            try {
                return DateUtils.toLocalDate((String) value);
            } catch (ParseException ignore) {
            }
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }
}
