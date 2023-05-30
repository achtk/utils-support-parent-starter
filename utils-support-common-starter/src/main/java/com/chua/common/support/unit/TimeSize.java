package com.chua.common.support.unit;

import com.chua.common.support.file.univocity.parsers.common.fields.FieldSet;
import com.chua.common.support.function.Joiner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间
 * @author CH
 */
public final class TimeSize implements Comparable<TimeSize> {

    private final Long ms;

    private static final Long SECOND = 1000L;
    private static final Long MIN = 60L * SECOND;
    private static final Long HOUR = 60L * MIN;
    private static final Long DAY = 24L * HOUR;
    private static final Long WEEK = 7L * HOUR;

    private static final Long MONTH = LocalDate.now().getDayOfMonth() * DAY;
    private static final Long YEAR = LocalDate.now().getDayOfYear() * DAY;

    public TimeSize(long ms) {
        this.ms = ms;
    }

    /**
     * 年
     * @param i 几年
     * @return this
     */
    public static TimeSize ofYear(long i) {
        return new TimeSize(i * YEAR);
    }
    /**
     * 月
     * @param i 几月
     * @return this
     */
    public static TimeSize ofMonth(long i) {
        return new TimeSize(i * MONTH);
    }
    /**
     * 周
     * @param i 几周
     * @return this
     */
    public static TimeSize ofWeek(long i) {
        return new TimeSize(i * WEEK);
    }
    /**
     * 天
     * @param i 几天
     * @return this
     */
    public static TimeSize ofDay(long i) {
        return new TimeSize(i * DAY);
    }
    /**
     * 小时
     * @param i 几小时
     * @return this
     */
    public static TimeSize ofHour(long i) {
        return new TimeSize(i * HOUR);
    }
    /**
     * 分
     * @param i 几分
     * @return this
     */
    public static TimeSize ofMin(long i) {
        return new TimeSize(i * MIN);
    }
    /**
     * 秒
     * @param i 几秒
     * @return this
     */
    public static TimeSize ofSecond(long i) {
        return new TimeSize(i * SECOND);
    }

    /**
     * 解析时间
     * @param time 时间
     * @return this
     */
    public static TimeSize of(String time) {
        time = time.toUpperCase();
        Pattern pattern = TimeUnit.getPattern();
        Matcher matcher = pattern.matcher(time);
        TimeSize timeSize = TimeSize.ofMs(0);
        while (matcher.find()) {
            timeSize = timeSize.add(TimeUnit.parse(matcher.group()));
        }

        return timeSize;
    }

    /**
     * 毫秒
     * @param l 毫秒
     * @return 毫秒
     */
    public static TimeSize ofMs(long l) {
        return new TimeSize(l);
    }


    @Override
    public int compareTo(TimeSize o) {
        return Long.compare(this.ms, o.ms);
    }

    /**
     * 追加
     * @param timeSize 时间
     * @return this
     */
    public TimeSize add(TimeSize timeSize) {
        return new TimeSize(this.ms + timeSize.ms);
    }

    /**
     * 毫秒
     * @return 毫秒
     */
    public long toMillis() {
        return ms;
    }

    /**
     * 秒
     * @return 秒
     */
    public long toSecond() {
        return ms / 1000L;
    }

    /**
     * 分
     * @return 分
     */
    public float toMin() {
        return toSecond() / 60f;
    }

    /**
     * 小时
     * @return 小时
     */
    public float toHour() {
        return toMin() / 60f;
    }

    /**
     * 天
     * @return 天
     */
    public float toDay() {
        return toHour() / 24f;
    }
}
