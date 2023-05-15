package com.chua.common.support.converter.definition;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * boolean类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/26
 */
public class CalendarTypeConverter implements TypeConverter<Calendar> {

    @Override
    public Class<Calendar> getType() {
        return Calendar.class;
    }

    @Override
    public Calendar convert(Object value) {
        if (value instanceof Date) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime((Date) value);
            return calendar;
        }

        if (value instanceof LocalDateTime) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(Date.from(((LocalDateTime) value).atZone(ZoneOffset.systemDefault()).toInstant()));
            return calendar;
        }

        if (value instanceof LocalDate) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(Date.from(((LocalDate) value).atStartOfDay(ZoneOffset.systemDefault()).toInstant()));
            return calendar;
        }

        if (value instanceof LocalTime) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(Date.from(Instant.ofEpochSecond(((LocalTime) value).getSecond(), ((LocalTime) value).getNano())));
            return calendar;
        }

        return null;
    }
}
