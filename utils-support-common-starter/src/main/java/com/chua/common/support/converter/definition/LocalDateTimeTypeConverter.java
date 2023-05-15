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
 * LocalDateTime
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/26
 */
public class LocalDateTimeTypeConverter implements TypeConverter<LocalDateTime> {

    @Override
    public LocalDateTime convert(Object value) {
        if (null == value) {
            return null;
        }

        if (isAssignableFrom(value, LocalDateTime.class)) {
            return (LocalDateTime) value;
        }

        if (isAssignableFrom(value, Date.class)) {
            return DateUtils.toLocalDateTime((Date) value);
        }

        if (isAssignableFrom(value, LocalDate.class)) {
            return DateUtils.toLocalDateTime((LocalDate) value);
        }

        if (isAssignableFrom(value, LocalTime.class)) {
            return DateUtils.toLocalDateTime((LocalTime) value);
        }

        if (isAssignableFrom(value, Long.class)) {
            return DateUtils.toLocalDateTime((Long) value);
        }

        if (isAssignableFrom(value, Calendar.class)) {
            return DateUtils.toLocalDateTime((Calendar) value);
        }

        if (isAssignableFrom(value, Instant.class)) {
            return DateUtils.toLocalDateTime((Instant) value);
        }

        if (isAssignableFrom(value, String.class)) {
            try {
                return DateUtils.toLocalDateTime((String) value);
            } catch (ParseException ignore) {
            }
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }
}
