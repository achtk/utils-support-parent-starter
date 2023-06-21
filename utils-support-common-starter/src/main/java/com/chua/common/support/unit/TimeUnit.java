package com.chua.common.support.unit;

import com.chua.common.support.function.Joiner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 时间
 * <pre>
 *     MS   1ms     1ms
 *     S    1s      1,000ms
 *     MIN  1min    60,000ms
 *     H    1h      3600,000ms
 *     D    1d      78640,000ms
 * </pre>
 * @author CH
 */
@Getter
@AllArgsConstructor
public enum TimeUnit {
    /**
     * 年
     */
    YEAR("Y", TimeSize.ofYear(1)),
    /**
     * 月
     */
    MONTH("M", TimeSize.ofMonth(1)),
    /**
     * 周
     */
    WEEK("W", TimeSize.ofWeek(1)),
    /**
     * 日
     */
    DAY("D", TimeSize.ofDay(1)),
    /**
     * 时
     */
    HOUR("H", TimeSize.ofHour(1)),
    /**
     * 分
     */
    MIN("MIN", TimeSize.ofMin(1)),
    /**
     * 秒
     */
    SECOND("S", TimeSize.ofSecond(1));

    private final String unit;
    private final TimeSize timeSize;

    public static TimeSize parse(String time) {
        time = time.toUpperCase();
        TimeUnit[] values = TimeUnit.values();
        Arrays.sort(values, (o1, o2) -> Integer.compare(o2.ordinal(), o1.ordinal()));
        for (TimeUnit timeUnit : values) {
            if(time.endsWith(timeUnit.getUnit()) || time.endsWith(timeUnit.name())) {
                return timeUnit.parse(timeUnit, time);
            }
        }
        return TimeSize.ofMs(0);
    }

    public TimeSize parse(TimeUnit timeUnit, String time) {
        if(timeUnit == YEAR ||
                timeUnit == MONTH ||
                timeUnit == WEEK ||
                timeUnit == DAY
        ) {
            Period period = Period.parse("P" + time);
            return TimeSize.ofYear(period.getYears())
                    .add(TimeSize.ofMonth(period.getMonths()))
                    .add(TimeSize.ofDay(period.getDays()));
        }

        time = time.replace("MIN", "M");
        Duration duration = Duration.parse("PT" + time);
        return TimeSize.ofSecond(duration.getSeconds());

    }

    static Pattern PATTERN;

    public static Pattern getPattern() {
        if(null != PATTERN) {
            return PATTERN;
        }

        List<String> timePattern = new LinkedList<>();
        for (TimeUnit timeUnit : TimeUnit.values()) {
            timePattern.add("(\\d+)" +timeUnit.getUnit());
            timePattern.add("(\\d+)" +timeUnit.name());
        }
        return (PATTERN = Pattern.compile(Joiner.on("|").join(timePattern)));
    }
}
