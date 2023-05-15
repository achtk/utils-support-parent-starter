package com.chua.common.support.converter.definition;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * TimeZone 转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class TimeZoneTypeConverter implements TypeConverter<TimeZone> {

    @Override
    public Class<TimeZone> getType() {
        return TimeZone.class;
    }

    @Override
    public TimeZone convert(Object value) {
        if (value instanceof ZoneId) {
            return TimeZone.getTimeZone((ZoneId) value);
        }

        if (value instanceof String) {
            return TimeZone.getTimeZone((String) value);
        }
        return null;
    }
}
