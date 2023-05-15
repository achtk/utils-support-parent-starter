package com.chua.common.support.date.format;

import com.chua.common.support.date.DateUtils;
import com.chua.common.support.utils.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * jdk解析器
 * <p>
 * All letters 'A' to 'Z' and 'a' to 'z' are reserved as pattern letters. The following pattern letters are defined:
 * Symbol  Meaning                     Presentation      Examples
 * ------  -------                     ------------      -------
 * G       era                         text              AD; Anno Domini; A
 * u       year                        year              2004; 04
 * y       year-of-era                 year              2004; 04
 * D       day-of-year                 number            189
 * M/L     month-of-year               number/text       7; 07; Jul; July; J
 * d       day-of-month                number            10
 * <p>
 * Q/q     quarter-of-year             number/text       3; 03; Q3; 3rd quarter
 * Y       week-based-year             year              1996; 96
 * w       week-of-week-based-year     number            27
 * W       week-of-month               number            4
 * E       day-of-week                 text              Tue; Tuesday; T
 * e/c     localized day-of-week       number/text       2; 02; Tue; Tuesday; T
 * F       week-of-month               number            3
 * <p>
 * a       am-pm-of-day                text              PM
 * h       clock-hour-of-am-pm (1-12)  number            12
 * K       hour-of-am-pm (0-11)        number            0
 * k       clock-hour-of-am-pm (1-24)  number            0
 * <p>
 * H       hour-of-day (0-23)          number            0
 * m       minute-of-hour              number            30
 * s       second-of-minute            number            55
 * S       fraction-of-second          fraction          978
 * A       milli-of-day                number            1234
 * n       nano-of-second              number            987654321
 * N       nano-of-day                 number            1234000000
 * <p>
 * V       time-zone ID                zone-id           America/Los_Angeles; Z; -08:30
 * z       time-zone name              zone-name         Pacific Standard Time; PST
 * O       localized zone-offset       offset-O          GMT+8; GMT+08:00; UTC-08:00;
 * X       zone-offset 'Z' for zero    offset-X          Z; -08; -0830; -08:30; -083015; -08:30:15;
 * x       zone-offset                 offset-x          +0000; -08; -0830; -08:30; -083015; -08:30:15;
 * Z       zone-offset                 offset-Z          +0000; -0800; -08:00;
 * </p>
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public class SimpleDateTimeFormat implements DateTimeFormat {

    private ZoneId timeZone = ZoneId.systemDefault();
    private Locale locale = Locale.getDefault();
    private Chronology chronology;

    private final static String[] DATE_FORMATS = {
            "EEE, d MMM yyyy HH:mm:ss z",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ssZ",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyyMMddHHmmss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyyMMddHHmmss",
            "yyyy-MM-dd HH:mm",
            "yyyyMMddHHmm",
            "yyyy-MM-dd HH",
            "yyyyMMddHH",
            "yyyy-MM-dd",
            "yyyyMMdd",
            "yyyy-MM",
            "yyyyMM"
    };

    @Override
    public DateTimeFormat withTimeZone(TimeZone timeZone) {
        this.timeZone = Optional.ofNullable(timeZone).orElse(TimeZone.getDefault()).toZoneId();
        return this;
    }

    @Override
    public DateTimeFormat withZone(ZoneId zone) {
        this.timeZone = Optional.ofNullable(zone).orElse(zone);
        return this;
    }

    @Override
    public ZoneId getZone() {
        return timeZone;
    }

    @Override
    public DateTimeFormat withChronology(Chronology chronology) {
        this.chronology = Optional.ofNullable(chronology).orElse(IsoChronology.INSTANCE);
        return this;
    }

    @Override
    public DateTimeFormat withLocale(Locale locale) {
        this.locale = Optional.ofNullable(locale).orElse(Locale.getDefault());
        return this;
    }

    @Override
    public LocalDateTime parse(String timeStr) {
        Date date = null;
        try {
            date = DateUtils.parseDateWithLeniency(timeStr, DATE_FORMATS, true);
        } catch (ParseException ignored) {
        }
        return parse(date);
    }

    @Override
    public LocalDateTime parse(String timeStr, String pattern) {
        if (StringUtils.isNullOrEmpty(timeStr)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        formatter.withZone(timeZone);
        formatter.withLocale(locale);
        formatter.withChronology(chronology);
        return LocalDateTime.parse(timeStr, formatter);
    }

    @Override
    public LocalDateTime parse(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), timeZone);
    }
}
