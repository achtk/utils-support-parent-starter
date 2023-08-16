package com.chua.common.support.lang.date;

import com.chua.common.support.lang.date.constant.DateFormatConstant;
import com.chua.common.support.utils.NumberUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.getInstance;

/**
 * 日期工具类<br />
 * 部分工具类来自Apache
 * 包含Date、LocalDate、LocalDateTime、LocalTime、Instant、ZonedDateTime、YearMonth、Timestamp和long等互相转换<br>
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/21
 */
public class DateUtils {
    /**
     * T
     */
    public static final String SYMBOL_VIRTUAL_T = "T";
    /**
     * 基础类型(double)
     */
    public static final String SYMBOL_VIRTUAL_D = "D";
    /**
     * 3
     */
    public static final int THIRD = 3;
    public static final int ONE_DAY = 24 * 60 * 60;
    public static final int ONE_HOUR = 60;
    public static final int ONE_MINUTE = ONE_HOUR;
    public static final int ACCURACY_HOURS = 4;
    public static final int ACCURACY_MINUTES = 5;
    public static final int ACCURACY_SECONDS = 6;
    public static final int ACCURACY_MILLISECONDS = 7;
    public static final int ACCURACY_MILLISECONDS_FORCED = 8;
    /**
     * 每秒毫秒数
     */
    public static final int MILLISECONDS_PER_SECONDE = 1000;
    /**
     * 每分毫秒数 60*1000
     */
    public static final int MILLISECONDS_PER_MINUTE = 60000;
    /**
     * 每小时毫秒数 36*60*1000
     */
    public static final int MILLISECONDS_PER_HOUR = 3600000;
    /**
     * 每天毫秒数 24*60*60*1000;
     */
    public static final long MILLISECONDS_PER_DAY = 86400000;
    public static final String WEEK = "week";
    public static final int WEEK_DAY = 7;
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    /**
     * Hours per day.
     */
    static final int HOURS_PER_DAY = 24;
    /**
     * Minutes per hour.
     */
    static final int MINUTES_PER_HOUR = 60;
    /**
     * Minutes per day.
     */
    static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    /**
     * Seconds per minute.
     */
    static final int SECONDS_PER_MINUTE = 60;
    /**
     * Seconds per hour.
     */
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    /**
     * Seconds per day.
     */
    static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    /**
     * Milliseconds per day.
     */
    static final long MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L;
    /**
     * Microseconds per day.
     */
    static final long MICROS_PER_DAY = SECONDS_PER_DAY * 1000_000L;
    /**
     * Nanos per second.
     */
    static final long NANOS_PER_SECOND = 1000_000_000L;
    /**
     * Nanos per minute.
     */
    static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
    /**
     * Nanos per hour.
     */
    static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
    /**
     * Nanos per day.
     */
    static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;
    /**
     * unix 时间长度
     */
    private static final int UNIX_LENGTH = 10;
    /**
     * 毫秒长度
     */
    private static final int MILLISECOND = 13;
    /**
     * =====================================时间元素===============================================
     */
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";
    /**
     * 星期一
     */
    private static final String MONDAY = "MONDAY";
    /**
     * 星期二
     */
    private static final String TUESDAY = "TUESDAY";
    /**
     * 星期三
     */
    private static final String WEDNESDAY = "WEDNESDAY";
    /**
     * 星期四
     */
    private static final String THURSDAY = "THURSDAY";
    /**
     * 星期五
     */
    private static final String FRIDAY = "FRIDAY";
    /**
     * 星期六
     */
    private static final String SATURDAY = "SATURDAY";
    /**
     * 星期日
     */
    private static final String SUNDAY = "SUNDAY";
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?" +
                            "(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?",
                    Pattern.CASE_INSENSITIVE);
    private final static String[] DATE_FORMATS = {
            "EEE, d MMM yyyy HH:mm:ss z",
            "EEE MMM dd HH:mm:ss zzz yyyy",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ssZ",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy年MM月dd日 HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyyMMddHHmmss",
            "HH:mm:ss",
            "HH:mm",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy年MM月dd日 HH:mm",
            "yyyy-MM-dd HH:mm",
            "yyyyMMddHHmm",
            "yyyy/MM/dd HH:mm",
            "yyyy年MM月dd日 HH",
            "yyyy-MM-dd HH",
            "yyyyMMddHH",
            "yyyy/MM/dd HH",
            "yyyy年MM月dd日",
            "yyyy-MM-dd",
            "yyyyMMdd",
            "yyyy/MM/dd",
            "yyyy年MM月",
            "yyyy-MM",
            "yyyy/MM",
            "yyyy年"
    };
    private static final int INDEX_NOT_FOUND = -1;

    /**
     * 持续时间
     *
     * @param time 持续时间
     * @return 持续时间
     */
    public static Duration toDuration(String time) {
        return Duration.parse("PT" + time);
    }

    /**
     * 持续时间
     *
     * @param time 持续时间
     * @return 持续时间
     */
    public static Period toPeriod(String time) {
        return Period.parse("P" + time);
    }


    /**
     * 判断{date1}是否在{date2}之后
     *
     * @param date1 时间
     * @param date2 时间
     * @return {date1}在{date2}之后返回true
     * @see NullPointerException
     */
    public Boolean after(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(date1.toInstant(), DEFAULT_ZONE_ID);
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date2.toInstant(), DEFAULT_ZONE_ID);
        return localDateTime1.isAfter(localDateTime2);
    }

    /**
     * 判断{date1}是否在{date2}之前
     *
     * @param date1 时间
     * @param date2 时间
     * @return {date1}在{date2}之前返回true
     * @see NullPointerException
     */
    public Boolean before(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(date1.toInstant(), DEFAULT_ZONE_ID);
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date2.toInstant(), DEFAULT_ZONE_ID);
        return localDateTime1.isBefore(localDateTime2);
    }

    /**
     * 判断{date1}, {date2}是否相等
     *
     * @param date1 时间
     * @param date2 时间
     * @return {date1}, {date2}是否相等返回true
     * @see NullPointerException
     */
    public Boolean equal(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(date1.toInstant(), DEFAULT_ZONE_ID);
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date2.toInstant(), DEFAULT_ZONE_ID);
        return localDateTime1.isEqual(localDateTime2);
    }

    /**
     * 获取{date}前{beforeOrAfter}天
     *
     * @param date          时间(默认当天)
     * @param beforeOrAfter < 0 前n天, > 0 后n天
     * @return 前{beforeOrAfter}天
     */
    public Date getDayOfBeforeOrAfter(Date date, int beforeOrAfter) {
        if (null == date) {
            date = new Date();
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        localDateTime.plusDays(beforeOrAfter);
        return toDate(localDateTime);
    }

    /**
     * 获取{date}前天
     *
     * @param date 时间(默认当天)
     * @return 前天
     */
    public Date getDayOfYearday(Date date) {
        return getDayOfBeforeOrAfter(date, -1);
    }

    /**
     * 获取{date}所在月的第一天
     *
     * @param date 时间(默认当天)
     * @return 所在月的第一天
     */
    public Date getFirstDayOfMonth(Date date) {
        if (null == date) {
            date = new Date();
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        //获取月的第一天0时0分0秒
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return toDate(localDateTime, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{date}所在周的第一天
     *
     * @param date 时间(默认当天)
     * @return 所在周的第一天
     */
    public Date getFirstDayOfWeek(Date date) {
        if (null == date) {
            date = new Date();
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        localDateTime.with(DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .withNano(0);
        return DateUtils.toDate(localDateTime);
    }

    /**
     * {date}的开始时间
     *
     * @param date 时间
     * @return 时间
     */
    public Date getFirstTimeOfDay(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
        localDateTime = localDateTime
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .withNano(0);

        return toDate(localDateTime);
    }

    /**
     * 获取{date}所在月的最后一天
     *
     * @param date 时间(默认当天)
     * @return 所在月的最后一天
     */
    public Date getLastDayOfMonth(Date date) {
        if (null == date) {
            date = new Date();
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);

        //获取月的最后一天的23点59分59秒
        localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        return toDate(localDateTime);
    }

    /**
     * 获取{date}所在周的最后一天
     *
     * @param date 时间(默认当天)
     * @return 所在周的最后一天
     */
    public Date getLastDayOfWeek(Date date) {
        if (null == date) {
            date = new Date();
        }

        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        localDateTime.with(DayOfWeek.SUNDAY)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .with(ChronoField.MILLI_OF_SECOND, 999);
        return DateUtils.toDate(localDateTime);
    }

    /**
     * {date}的结束时间
     *
     * @param date 时间
     * @return 时间
     */
    public Date getLastTimeOfDay(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
        localDateTime = localDateTime
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .with(ChronoField.MILLI_OF_SECOND, 999);

        return toDate(localDateTime);
    }

    /**
     * 周几
     *
     * @return 1:周一, 2: 周二
     */
    public int getWeek() {
        Date today = new Date();
        Calendar c = getInstance();
        c.setTime(today);
        return c.get(DAY_OF_WEEK) - 1;
    }

    /**
     * 周几
     *
     * @return 1:周一, 2: 周二
     */
    public int getWeek(Date date) {
        Calendar c = getInstance();
        c.setTime(date);
        return c.get(DAY_OF_WEEK) - 1;
    }

    /**
     * 获取两个时间的之间所有时间(第一天开始)
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间的之间所有时间
     */
    public static List<Date> asRange(Date before, Date after) {
        List<Date> result = new ArrayList<>();
        LocalDate beforeLocalDate = toLocalDate(before);
        LocalDate afterLocalDate = toLocalDate(after);
        long beforeEpochMilli = beforeLocalDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        if (beforeEpochMilli == before.getTime()) {
            result.add(before);
            beforeLocalDate = beforeLocalDate.plusDays(1);
        }
        result.add(toDate(afterLocalDate));
        while (beforeLocalDate.isBefore(afterLocalDate)) {
            result.add(toDate(beforeLocalDate));
            beforeLocalDate = beforeLocalDate.plusDays(1);
        }
        result.sort((o1, o2) -> o1.after(o2) ? 1 : -1);
        return result;
    }

    /**
     * 当前时间戳
     *
     * @return 当前时间时间戳
     */
    public static long current() {
        return System.currentTimeMillis();
    }

    /**
     * 当前时间
     *
     * @return 当前时间
     */
    public static String currentString() {
        return format(current(), DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date()) =
     * </p>
     *
     * @param str 时间
     * @return 时间
     */
    public static Date format(String str, String pattern) throws ParseException {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.parse(str);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date()) =
     * </p>
     *
     * @param zonedDateTime 时间
     * @param pattern       格式化
     * @return 时间
     */
    public static String format(ZonedDateTime zonedDateTime, String pattern) throws ParseException {
        return format(zonedDateTime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date()) =
     * </p>
     *
     * @param zonedDateTime 时间
     * @param df            格式化
     * @return 时间
     */
    public static String format(ZonedDateTime zonedDateTime, DateTimeFormatter df) throws ParseException {
        return zonedDateTime.format(df);
    }

    /**
     * 格式化时间
     * <br /> 默认格式： yyyy-MM-dd HH:mm:ss
     * {@link #format(Date, String)}
     * <pre>
     *   DateHelper.format(new Date()) =  比如：2020-05-23 17:06:30
     * </pre>
     *
     * @param date 时间
     * @return 格式化时间
     * @see #format(Date, String)
     */
    public static String format(TemporalAccessor date) {
        if (date instanceof LocalDateTime) {
            return format((LocalDateTime) date, DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
        }

        if (date instanceof LocalDate) {
            return format((LocalDate) date, DateFormatConstant.YYYY_MM_DD);
        }

        if (date instanceof LocalTime) {
            return format((LocalTime) date, DateFormatConstant.HH_MM_SS);
        }

        return null;
    }

    /**
     * 格式化时间
     * <br /> 默认格式： yyyy-MM-dd HH:mm:ss
     * {@link #format(Date, String)}
     * <pre>
     *   DateHelper.format(new Date()) =  比如：2020-05-23 17:06:30
     * </pre>
     *
     * @param date 时间
     * @return 格式化时间
     * @see #format(Date, String)
     */
    public static String format(Date date) {
        return format(date, DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date(), "yyyy-MM-dd") =
     * </p>
     *
     * @param localDateTime 时间
     * @param pattern       表达式
     * @return 格式化时间
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date(), "yyyy-MM-dd") =
     * </p>
     *
     * @param localTime 时间
     * @param pattern   表达式
     * @return 格式化时间
     */
    public static String format(LocalTime localTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localTime);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date(), "yyyy-MM-dd") =
     * </p>
     *
     * @param localDate 日期
     * @param pattern   表达式
     * @return 格式化时间
     */
    public static String format(LocalDate localDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(new Date(), "yyyy-MM-dd") =
     * </p>
     *
     * @param date    时间
     * @param pattern 表达式
     * @return 格式化时间
     */
    public static String format(Date date, String pattern) {
        if (null == date || null == pattern) {
            return null;
        }
        DateFormat df = createDefaultDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 格式化时间
     *
     * @param date       时间
     * @param dateFormat 时间格式化
     * @return 格式化时间
     */
    public static String format(Date date, DateFormat dateFormat) {
        if (null == date) {
            return null;
        }
        DateFormat df = null == dateFormat ? createDefaultDateFormat(DateFormatConstant.YYYY_MM_DD_HH_MM_SS) : dateFormat;
        return df.format(date);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(1111) =
     * </p>
     *
     * @param time 时间
     * @return 格式化时间
     */
    public static String format(long time) {
        return format(time, DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化时间
     * <p>
     * DateHelper.format(1111, "yyyy-MM-dd") =
     * </p>
     *
     * @param time    时间
     * @param pattern 表达式
     * @return 格式化时间
     */
    public static String format(long time, String pattern) {
        DateFormat df = createDefaultDateFormat(pattern);
        return df.format(time);
    }

    /**
     * 格式化时间
     *
     * @param time       时间
     * @param dateFormat 时间格式化
     * @return 格式化时间
     */
    public static String format(long time, DateFormat dateFormat) {
        DateFormat df = null == dateFormat ? createDefaultDateFormat(DateFormatConstant.YYYY_MM_DD_HH_MM_SS) : dateFormat;
        return df.format(time);
    }

    /**
     * 获取当前日期之后（之后）的节点事件<br>
     * <ul>
     * 比如当前时间为：2019-03-30 10:20:30
     * </ul>
     * <li>node="hour",num=5L:2019-03-30 15:20:30</li>
     * <li>node="day",num=1L:2019-03-31 10:20:30</li>
     * <li>node="year",num=1L:2020-03-30 10:20:30</li>
     *
     * @param node 节点元素（“year”,"month","week","day","huor","minute","second"）
     * @param num  第几天（+：之后，-：之前）
     * @return 之后或之后的日期
     */
    public static String getAfterOrPreNowTime(final String node, final Long num) {
        return getAfterOrPreNowTime(node, num, DateFormatConstant.YYYY_MM_DD_HH_MM_SS_FMT);
    }

    /**
     * 获取当前日期之后（之后）的节点事件<br>
     * <ul>
     * 比如当前时间为：2019-03-30 10:20:30
     * </ul>
     * <li>node="hour",num=5L:2019-03-30 15:20:30</li>
     * <li>node="day",num=1L:2019-03-31 10:20:30</li>
     * <li>node="year",num=1L:2020-03-30 10:20:30</li>
     *
     * @param node              节点元素（“year”,"month","week","day","huor","minute","second"）
     * @param num               第几天（+：之后，-：之前）
     * @param dateTimeFormatter 格式化当前时间格式
     * @return 之后或之后的日期
     */
    public static String getAfterOrPreNowTime(String node, Long num, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime now = LocalDateTime.now();
        if (HOUR.equals(node)) {
            return now.plusHours(num).format(dateTimeFormatter);
        } else if (DAY.equals(node)) {
            return now.plusDays(num).format(dateTimeFormatter);
        } else if (WEEK.equals(node)) {
            return now.plusWeeks(num).format(dateTimeFormatter);
        } else if (MONTH.equals(node)) {
            return now.plusMonths(num).format(dateTimeFormatter);
        } else if (YEAR.equals(node)) {
            return now.plusYears(num).format(dateTimeFormatter);
        } else if (MINUTE.equals(node)) {
            return now.plusMinutes(num).format(dateTimeFormatter);
        } else if (SECOND.equals(node)) {
            return now.plusSeconds(num).format(dateTimeFormatter);
        } else {
            return "Node is Error!";
        }
    }

    /**
     * 获取当前时间前{num}天
     *
     * @param num 天数
     * @return 获取当前时间前{num}天
     */
    public static List<Date> getBeforeDate(final int num) {
        assert num > 0 : "num不能小于1";

        LocalDate localDate = LocalDate.now();
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            dateList.add(Date.from(localDate.minusDays(i).atStartOfDay().atZone(DEFAULT_ZONE_ID).toInstant()));
        }
        return dateList;
    }

    /**
     * 获取当前时间 {@link #currentString()}
     *
     * @return 当前时间
     */
    public static String getCurrentDate() {
        return currentString();
    }

    /**
     * 到下一分钟0秒的毫秒数
     *
     * @param rightNow 当前时间
     * @return the int 到下一分钟的毫秒数
     */
    public static int getDelayToNextMinute(long rightNow) {
        return (int) (MILLISECONDS_PER_MINUTE - (rightNow % MILLISECONDS_PER_MINUTE));
    }

    /**
     * 上一分钟的最后一毫秒
     *
     * @param rightNow 当前时间
     * @return 上一分钟的最后一毫秒
     */
    public static long getPreMinuteMills(long rightNow) {
        return rightNow - (rightNow % MILLISECONDS_PER_MINUTE) - 1;
    }

    /**
     * 获取毫秒
     *
     * @return 毫秒
     */
    public static long getTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取纳秒
     *
     * @return 纳秒
     */
    public static long getTimeNanos() {
        return System.nanoTime();
    }

    /**
     * 获取最接近的时间点
     *
     * @param localTime      校验时间
     * @param localTimeStirs 基准时间
     * @return 最接近的时间
     */
    public static LocalDateTime nearLocalDateTime(LocalDateTime localTime, String... localTimeStirs) {
        LocalDateTime[] localTimes = new LocalDateTime[localTimeStirs.length];
        int index = 0;
        for (String timeStir : localTimeStirs) {
            localTimes[index] = LocalDateTime.parse(timeStir);
        }
        return nearLocalDateTime(localTime, localTimes);
    }

    /**
     * 获取最接近的时间点
     *
     * @param localDateTime  校验时间
     * @param localDateTimes 基准时间
     * @return 最接近的时间
     */
    public static LocalDateTime nearLocalDateTime(LocalDateTime localDateTime, LocalDateTime... localDateTimes) {
        Map<Long, LocalDateTime> result = new HashMap<>(localDateTimes.length);
        long nanoOfDay = localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        for (LocalDateTime time : localDateTimes) {
            result.put(Math.abs(nanoOfDay - time.getNano()), time);
        }
        LocalDateTime minLocalTime = null;
        long min = Long.MAX_VALUE;
        for (Map.Entry<Long, LocalDateTime> entry : result.entrySet()) {
            Long key = entry.getKey();
            if (key < min) {
                min = key;
                minLocalTime = entry.getValue();
            }
        }
        return minLocalTime;
    }

    /**
     * 获取最接近的时间点
     *
     * @param localTime      校验时间
     * @param localTimeStirs 基准时间
     * @return 最接近的时间
     */
    public static LocalTime nearLocalTime(LocalTime localTime, String... localTimeStirs) {
        LocalTime[] localTimes = new LocalTime[localTimeStirs.length];
        int index = 0;
        for (String timeStir : localTimeStirs) {
            localTimes[index++] = LocalTime.parse(timeStir);
        }
        return nearLocalTime(localTime, localTimes);
    }

    /**
     * 获取最接近的时间点
     *
     * @param localTime  校验时间
     * @param localTimes 基准时间
     * @return 最接近的时间
     */
    public static LocalTime nearLocalTime(LocalTime localTime, LocalTime... localTimes) {
        Map<Long, LocalTime> result = new HashMap<>(localTimes.length);
        long secondOfDay = localTime.toSecondOfDay();
        for (LocalTime time : localTimes) {
            result.put(Math.abs(secondOfDay - time.toSecondOfDay()), time);
        }
        LocalTime minLocalTime = null;
        long min = Long.MAX_VALUE;
        for (Map.Entry<Long, LocalTime> entry : result.entrySet()) {
            Long key = entry.getKey();
            if (key < min) {
                min = key;
                minLocalTime = entry.getValue();
            }
        }
        return minLocalTime;
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有任何匹配的解析模式，则抛出ParseException。</ p>
     * 解析器将对解析日期宽容。
     *
     * @param str 解析日期，不为null
     * @return 日期
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式（或没有合适的日期）
     */
    public static Calendar parseCalendar(String str) throws ParseException {
        Calendar calendar = getInstance();
        calendar.setTime(parseDateWithLeniency(str, DATE_FORMATS, true));
        return calendar;
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有任何匹配的解析模式，则抛出ParseException。</ p>
     * 解析器将对解析日期宽容。
     *
     * @param str           解析日期，不为null
     * @param parsePatterns 要使用的日期格式模式，请参见SimpleDateFormat，不为null
     * @return 日期
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式（或没有合适的日期）
     */
    public static Date parseDate(String str, String[] parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, parsePatterns, true);
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有任何匹配的解析模式，则抛出ParseException。</ p>
     * 解析器将对解析日期宽容。
     *
     * @param str 解析日期，不为null
     * @return 日期
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式（或没有合适的日期）
     */
    public static Date parseDate(String str) throws ParseException {
        if (str.length() > 10 && NumberUtils.isNumber(str)) {
            return new Date(Long.parseLong(str));
        }
        Date date = null;
        try {
            date = parseDateWithLeniency(str, DATE_FORMATS, false);
        } catch (ParseException ignored) {
        }
        return null == date ? parseDateWithLeniency(str, DATE_FORMATS, Locale.US) : date;
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有任何匹配的解析模式，则抛出ParseException。</ p>
     * 解析器将对解析日期宽容。
     *
     * @param str 解析日期，不为null
     * @return 日期
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式（或没有合适的日期）
     */
    public static Date parseDate(String str, final String parsePattern) throws ParseException {
        return parseDateWithLeniency(str, new String[]{parsePattern}, true);
    }

    /**
     * 时间戳epochMilli毫秒转Date
     *
     * @param epochMilli 时间戳
     * @return Date
     */
    public static Date parseDate(final Long epochMilli) {
        if (null == epochMilli) {
            return null;
        }
        int length = epochMilli.toString().length();

        long newLongValue = epochMilli;
        if (length == UNIX_LENGTH) {
            newLongValue *= ((Double) Math.pow(10D, MILLISECOND - length)).longValue();
        }

        return new Date(newLongValue);
    }

    /**
     * LocalDate -> Date
     *
     * @param localDate localDate
     * @return Date
     */
    public static Date parseDate(final LocalDate localDate) {
        return parseDate(localDate, null);
    }

    /**
     * LocalDate -> Date
     *
     * @param localDate localDate
     * @param zone      zone
     * @return Date
     */
    public static Date parseDate(final LocalDate localDate, final ZoneId zone) {
        if (null == localDate) {
            return null;
        }

        Instant instant = localDate.atStartOfDay(Optional.ofNullable(zone).orElse(DEFAULT_ZONE_ID)).toInstant();
        return Date.from(instant);
    }

    /**
     * Instant -> Date
     *
     * @param instant instant
     * @return Date
     */
    public static Date parseDate(final Instant instant) {
        return Date.from(Optional.ofNullable(instant).orElse(Instant.now()));
    }

    /**
     * date -> Date
     *
     * @param date date
     * @return Date
     */
    public static Date parseDate(final java.sql.Date date) {
        return new Date(date.getTime());
    }

    /**
     * time -> Date
     *
     * @param time time
     * @return Date
     */
    public static Date parseDate(final java.sql.Time time) {
        return toDate(time.toLocalTime());
    }

    /**
     * 日期转Date
     *
     * @param calendar 日期
     * @return Date
     */
    public static Date parseDate(final Calendar calendar) {
        return toDate(calendar.toInstant());
    }

    /**
     * LocalDate and LocalTime -> Date
     *
     * @param localDate localDate
     * @param localTime localTime
     * @param zone      zone
     * @return Date
     */
    public static Date parseDate(final LocalDate localDate, final LocalTime localTime, final ZoneId zone) {
        if (null == localDate || null == localTime) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return parseDate(localDateTime, zone);
    }

    /**
     * LocalDateTime -> Date
     *
     * @param localDateTime localTime
     * @return Date
     */
    public static Date parseDate(final LocalDateTime localDateTime) {
        return parseDate(localDateTime, null);
    }

    /**
     * LocalDateTime -> Date
     *
     * @param localDateTime localTime
     * @param zone          zone
     * @return Date
     */
    public static Date parseDate(final LocalDateTime localDateTime, final ZoneId zone) {
        if (null == localDateTime) {
            return null;
        }

        Instant instant = localDateTime.atZone(Optional.ofNullable(zone).orElse(DEFAULT_ZONE_ID)).toInstant();
        return Date.from(instant);
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有解析模式匹配，则抛出ParseException。</ p>
     *
     * @param str           解析日期，不为null
     * @param parsePatterns 要使用的日期格式模式，请参见SimpleDateFormat，不为null
     * @param loc           时区
     * @return Date
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式
     * @see Calendar
     */
    public static Date parseDateWithLeniency(String str, String[] parsePatterns, Locale loc) throws ParseException {
        for (String dateFormat : parsePatterns) {
            SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat, loc);
            Date parse = null;
            try {
                parse = sdf2.parse(str);
            } catch (ParseException ignored) {
            }
            if (null != parse) {
                return parse;
            }
        }
        return null;
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有解析模式匹配，则抛出ParseException。</ p>
     *
     * @param str           解析日期，不为null
     * @param parsePatterns 要使用的日期格式模式，请参见SimpleDateFormat，不为null
     * @param lenient       指定日期/时间解析。
     * @return Date
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式
     * @see Calendar
     */
    public static Date parseDateWithLeniency(String str, String[] parsePatterns, boolean lenient) throws ParseException {
        return parseDateWithLeniency(str, parsePatterns, lenient, null);
    }

    /**
     * <p>通过尝试各种不同的解析器来解析表示日期的字符串。</ p>
     * <p>解析将依次尝试每种解析模式。
     * 仅当解析了整个输入字符串时，解析才被认为是成功的。
     * 如果没有解析模式匹配，则抛出ParseException。</ p>
     *
     * @param str           解析日期，不为null
     * @param parsePatterns 要使用的日期格式模式，请参见SimpleDateFormat，不为null
     * @param lenient       指定日期/时间解析。
     * @param loc           时区
     * @return Date
     * @throws IllegalArgumentException 如果日期字符串或模式数组为null
     * @throws ParseException           如果没有合适的日期模式
     * @see Calendar
     */
    public static Date parseDateWithLeniency(String str, String[] parsePatterns, boolean lenient, Locale loc) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(lenient);
        if (null != loc) {
            parser.setCalendar(Calendar.getInstance(TimeZone.getDefault(), loc));
        }

        ParsePosition pos = new ParsePosition(0);
        for (String parsePattern : parsePatterns) {

            String pattern = parsePattern;

            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }

            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePattern.endsWith("ZZ")) {
                int signIdx = indexOfSignChars(str2, 0);
                while (signIdx >= 0) {
                    str2 = reformatTimezone(str2, signIdx);
                    signIdx = indexOfSignChars(str2, ++signIdx);
                }
            }

            Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }


        str = trimAllWhitespace(str).toUpperCase();
        if (str.endsWith(SYMBOL_VIRTUAL_D)) {
            str += "0S";
        }
        Matcher matcher = PATTERN.matcher("P".concat(str.contains("D") ? str.replace("D", "DT") : "T".concat(str)));
        if (matcher.matches()) {
            if (!SYMBOL_VIRTUAL_T.equals(matcher.group(THIRD))) {
                boolean negate = "-".equals(matcher.group(1));
                String dayMatch = matcher.group(2);
                String hourMatch = matcher.group(4);
                String minuteMatch = matcher.group(5);
                String secondMatch = matcher.group(6);
                if (dayMatch != null || hourMatch != null || minuteMatch != null || secondMatch != null) {
                    long daysAsSecs = parseNumber(str, dayMatch, SECONDS_PER_DAY, "days");
                    long hoursAsSecs = parseNumber(str, hourMatch, SECONDS_PER_HOUR, "hours");
                    long minsAsSecs = parseNumber(str, minuteMatch, SECONDS_PER_MINUTE, "minutes");
                    long seconds = parseNumber(str, secondMatch, 1, "seconds");
                    try {
                        long secondsOfPt = createSeconds(negate, daysAsSecs, hoursAsSecs, minsAsSecs, seconds, 0);
                        if (daysAsSecs == 0) {
                            return toDate(toDay(LocalTime.ofSecondOfDay(secondsOfPt)));
                        } else {
                            LocalDateTime localDateTime = LocalDateTime.now();
                            localDateTime = localDateTime.plusSeconds(secondsOfPt);
                            return toDate(localDateTime);
                        }
                    } catch (ArithmeticException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to a Duration: overflow", str, 0, ex);
                    }
                }
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    /**
     * YearMonth转Date
     * 注意dayOfMonth范围：1到31之间，最大值根据月份确定特殊情况，如2月闰年29，非闰年28
     * 如果要转换为当月最后一天，可以使用下面方法：toDateEndOfMonth(YearMonth)
     *
     * @param yearMonth YearMonth
     * @return Date
     */
    public static Date parserDate(YearMonth yearMonth) {
        Objects.requireNonNull(yearMonth, "yearMonth");
        return toDate(yearMonth.atDay(1));
    }

    /**
     * YearMonth转Date
     * 注意dayOfMonth范围：1到31之间，最大值根据月份确定特殊情况，如2月闰年29，非闰年28
     * 如果要转换为当月最后一天，可以使用下面方法：toDateEndOfMonth(YearMonth)
     *
     * @param yearMonth  YearMonth
     * @param dayOfMonth 天
     * @return Date
     */
    public static Date parserDate(YearMonth yearMonth, int dayOfMonth) {
        Objects.requireNonNull(yearMonth, "yearMonth");
        return toDate(yearMonth.atDay(dayOfMonth));
    }

    /**
     * ZonedDateTime转Date
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return Date
     */
    public static Date parserDate(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * localTime转Date
     *
     * @param localTime localeDate
     * @return Date
     * @see LocalDate
     * @see Date
     */
    public static Date toDate(LocalTime localTime) {
        if (null == localTime) {
            return null;
        }

        return toDate(Instant.ofEpochSecond(localTime.getSecond(), localTime.getNano()));
    }

    /**
     * localeDate转Date
     *
     * @param localDate localeDate
     * @return Date
     * @see LocalDate
     * @see Date
     */
    public static Date toDate(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }

        ZonedDateTime zonedDateTime = localDate.atStartOfDay(DEFAULT_ZONE_ID);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    /**
     * localeDate转Date
     *
     * @param localDateTime localDateTime
     * @return Date
     * @see LocalDate
     * @see Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime, DEFAULT_ZONE_ID);
    }

    /**
     * localeDate转Date
     *
     * @param localDateTime localDateTime
     * @param zoneId        时区
     * @return Date
     * @see LocalDate
     * @see Date
     */
    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (null == localDateTime) {
            return null;
        }
        return Date.from(localDateTime.atZone(Optional.ofNullable(zoneId).orElse(DEFAULT_ZONE_ID)).toInstant());
    }

    /**
     * java.sql.Date转Date
     *
     * @param date date
     * @return Date
     * @see LocalDate
     * @see Date
     * @see Instant
     */
    public static Date toDate(java.sql.Date date) {
        return date;
    }

    /**
     * java.sql.Date转Date
     *
     * @param date date
     * @return Date
     * @see LocalDate
     * @see Date
     * @see Instant
     */
    public static Date toDate(Timestamp date) {
        return date;
    }

    /**
     * instant转Date
     *
     * @param instant instant
     * @return Date
     * @see LocalDate
     * @see Date
     * @see Instant
     */
    public static Date toDate(Instant instant) {
        if (null == instant) {
            return null;
        }
        return Date.from(instant);
    }

    /**
     * long 转Date
     *
     * @param time time
     * @return Date
     * @see LocalDate
     * @see Date
     * @see Instant
     */
    public static Date toDate(long time) {
        return Date.from(Instant.ofEpochMilli(complementMilliseconds(time)));
    }

    /**
     * Date转时间戳
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param date Date
     * @return 时间戳
     */
    public static long toEpochMilli(Date date) {
        Objects.requireNonNull(date, "date");
        return date.getTime();
    }

    /**
     * Timestamp转时间戳
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param timestamp Timestamp
     * @return 时间戳
     */
    public static long toEpochMilli(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "timestamp");
        return timestamp.getTime();
    }

    /**
     * LocalDateTime转时间戳
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param localDateTime LocalDateTime
     * @return 时间戳
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }

    /**
     * LocalDate转时间戳
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param localDate LocalDate
     * @return 时间戳
     */
    public static long toEpochMilli(LocalDate localDate) {
        return toInstant(localDate).toEpochMilli();
    }

    /**
     * Instant转时间戳
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param instant Instant
     * @return 时间戳
     */
    public static long toEpochMilli(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return instant.toEpochMilli();
    }

    /**
     * ZonedDateTime转时间戳，注意，zonedDateTime时区必须和当前系统时区一致，不然会出现问题
     * 从1970-01-01T00:00:00Z开始的毫秒值
     *
     * @param zonedDateTime ZonedDateTime
     * @return 时间戳
     */
    public static long toEpochMilli(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * Date转Instant
     *
     * @param date Date
     * @return Instant
     */
    public static Instant toInstant(Date date) {
        Objects.requireNonNull(date, "date");
        return date.toInstant();
    }

    /**
     * Timestamp转Instant
     *
     * @param timestamp Timestamp
     * @return Instant
     */
    public static Instant toInstant(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "timestamp");
        return timestamp.toInstant();
    }

    /**
     * LocalDateTime转Instant
     *
     * @param localDateTime LocalDateTime
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant();
    }

    /**
     * LocalDate转Instant
     *
     * @param localDate LocalDate
     * @return Instant
     */
    public static Instant toInstant(LocalDate localDate) {
        return toLocalDateTime(localDate).atZone(DEFAULT_ZONE_ID).toInstant();
    }

    /**
     * LocalTime转Instant
     * 以当天的日期+LocalTime组成新的LocalDateTime转换为Instant
     *
     * @param localTime LocalTime
     * @return Instant
     */
    public static Instant toInstant(LocalTime localTime) {
        return toLocalDateTime(localTime).atZone(DEFAULT_ZONE_ID).toInstant();
    }

    /**
     * 时间戳epochMilli毫秒转Instant
     *
     * @param epochMilli 时间戳
     * @return Instant
     */
    public static Instant toInstant(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return Instant.ofEpochMilli(epochMilli);
    }

    /**
     * temporal转Instant
     *
     * @param temporal TemporalAccessor
     * @return Instant
     */
    public static Instant toInstant(TemporalAccessor temporal) {
        return Instant.from(temporal);
    }

    /**
     * ZonedDateTime转Instant
     * 注意，zonedDateTime时区必须和当前系统时区一致，不然会出现问题
     *
     * @param zonedDateTime ZonedDateTime
     * @return Instant
     */
    public static Instant toInstant(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toInstant();
    }

    /**
     * Date转LocalDate
     *
     * @param date Date
     * @return LocalDate
     * @throws NullPointerException if date is not valid
     */
    public static LocalDate toLocalDate(final Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * dateStr转LocalDate
     *
     * @param dateStr dateStr
     * @return LocalDate
     * @throws NullPointerException if date is not valid
     */
    public static LocalDate toLocalDate(final String dateStr) throws ParseException {
        return toLocalDateTime(dateStr).toLocalDate();
    }

    /**
     * Date转LocalDate
     *
     * @param calendar calendar
     * @return LocalDate
     * @throws NullPointerException if calendar is not valid
     */
    public static LocalDate toLocalDate(final Calendar calendar) {
        return toLocalDate(calendar.getTime());
    }
    /**
     * String转LocalDate
     *
     * @param str str
     * @param format format
     * @return LocalDate
     * @throws NullPointerException if localDateTime is not valid
     */
    public static LocalDate toLocalDate(String str, String format) {
        Objects.requireNonNull(str, "str");
        Objects.requireNonNull(format, "format");
        return toLocalDate(DateTimeFormatter.ofPattern(format).parse(str));
    }

    /**
     * LocalDateTime转LocalDate
     *
     * @param localDateTime LocalDateTime
     * @return LocalDate
     * @throws NullPointerException if localDateTime is not valid
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.toLocalDate();
    }

    /**
     * Instant转LocalDate
     *
     * @param instant Instant
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Instant instant) {
        return toLocalDateTime(instant).toLocalDate();
    }

    /**
     * 时间戳epochMilli毫秒转LocalDate
     *
     * @param epochMilli 时间戳
     * @return LocalDate
     */
    public static LocalDate toLocalDate(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return toLocalDateTime(epochMilli).toLocalDate();
    }

    /**
     * temporal转LocalDate
     *
     * @param temporal TemporalAccessor
     * @return LocalDate
     */
    public static LocalDate toLocalDate(TemporalAccessor temporal) {
        return LocalDate.from(temporal);
    }

    /**
     * ZonedDateTime转LocalDate
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return LocalDate
     */
    public static LocalDate toLocalDate(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toLocalDate();
    }

    /**
     * YearMonth转LocalDate
     * 注意dayOfMonth范围：1到31之间，最大值根据月份确定特殊情况，如2月闰年29，非闰年28
     * 如果要转换为当月最后一天，可以使用下面方法：toLocalDateEndOfMonth(YearMonth)
     *
     * @param yearMonth  YearMonth
     * @param dayOfMonth 天
     * @return LocalDate
     */
    public static LocalDate toLocalDate(YearMonth yearMonth, int dayOfMonth) {
        Objects.requireNonNull(yearMonth, "yearMonth");
        return yearMonth.atDay(dayOfMonth);
    }

    /**
     * YearMonth转LocalDate，转换为当月最后一天
     *
     * @param yearMonth YearMonth
     * @return LocalDate
     */
    public static LocalDate toLocalDateEndOfMonth(YearMonth yearMonth) {
        Objects.requireNonNull(yearMonth, "yearMonth");
        return yearMonth.atEndOfMonth();
    }

    /**
     * YearMonth转LocalDate，转换为当月第一天
     *
     * @param yearMonth YearMonth
     * @return LocalDate
     */
    public static LocalDate toLocalDateStartOfMonth(YearMonth yearMonth) {
        return toLocalDate(yearMonth, 1);
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(final Date date) {
        return toLocalDateTime(date, DEFAULT_ZONE_ID);
    }

    /**
     * calendar -> LocalDateTime
     *
     * @param calendar 日历
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(final Calendar calendar) {
        return toLocalDateTime(calendar.getTime());
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date    date
     * @param pattern 表达式
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(final String date, final String pattern) {
        try {
            return toLocalDateTime(parseDate(date, Optional.ofNullable(pattern).orElse(DateFormatConstant.YYYY_MM_DD_HH_MM_SS)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(final String date) throws ParseException {
        return toLocalDateTime(parseDate(date));
    }

    /**
     * Timestamp转LocalDateTime
     *
     * @param timestamp Timestamp
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "timestamp");
        return timestamp.toLocalDateTime();
    }

    /**
     * LocalDate转LocalDateTime
     *
     * @param localDate LocalDate
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay();
    }

    /**
     * LocalDate转LocalDateTime
     *
     * @param localDate LocalDate
     * @param localTime 时间
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate, LocalTime localTime) {
        Objects.requireNonNull(localDate, "localDate");
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * LocalTime转LocalDateTime
     * 以当天的日期+LocalTime组成新的LocalDateTime
     *
     * @param localTime LocalTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime);
    }

    /**
     * Instant转LocalDateTime
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
    }

    /**
     * 时间戳epochMilli毫秒转LocalDateTime
     *
     * @param epochMilli 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE_ID);
    }

    /**
     * temporal转LocalDateTime
     *
     * @param temporal TemporalAccessor
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }

    /**
     * ZonedDateTime转LocalDateTime
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date date
     * @param zone zone
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(final Date date, final ZoneId zone) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, Optional.ofNullable(zone).orElse(DEFAULT_ZONE_ID));
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date date
     * @param zone zone
     * @return LocalDateTime
     */
    public static LocalTime toLocalTime(final Date date, final ZoneId zone) {
        return toLocalDateTime(date, zone).toLocalTime();
    }

    /**
     * Calendar -> LocalDateTime
     *
     * @param calendar date
     * @return LocalDateTime
     * @throws NullPointerException if calendar is not valid
     */
    public static LocalTime toLocalTime(final Calendar calendar) {
        return toLocalDateTime(calendar.getTime()).toLocalTime();
    }

    /**
     * dateStr -> LocalDateTime
     *
     * @param dateStr dateStr
     * @return LocalDateTime
     * @throws NullPointerException if dateStr is not valid
     */
    public static LocalTime toLocalTime(final String dateStr) throws ParseException {
        if (NumberUtils.isNumber(dateStr)) {
            return LocalTime.ofSecondOfDay(Long.parseLong(dateStr));
        }
        return toLocalDateTime(dateStr).toLocalTime();
    }

    /**
     * Date转LocalTime
     *
     * @param date Date
     * @return LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }

    /**
     * long转LocalTime
     *
     * @param epochMilli 时间戳
     * @return LocalTime
     */
    public static LocalTime toLocalTime(long epochMilli) {
        return toLocalTime(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * LocalDateTime转LocalTime
     *
     * @param localDateTime LocalDateTime
     * @return LocalTime
     */
    public static LocalTime toLocalTime(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.toLocalTime();
    }

    /**
     * Instant转LocalTime
     *
     * @param instant Instant
     * @return LocalTime
     */
    public static LocalTime toLocalTime(Instant instant) {
        return toLocalDateTime(instant).toLocalTime();
    }

    /**
     * temporal转LocalTime
     *
     * @param temporal TemporalAccessor
     * @return LocalTime
     */
    public static LocalTime toLocalTime(TemporalAccessor temporal) {
        return LocalTime.from(temporal);
    }

    /**
     * ZonedDateTime转LocalTime
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return LocalTime
     */
    public static LocalTime toLocalTime(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toLocalTime();
    }

    /**
     * Date转Timestamp
     *
     * @param date Date
     * @return Timestamp
     */
    public static Timestamp toTimestamp(Date date) {
        Objects.requireNonNull(date, "date");
        return new Timestamp(date.getTime());
    }

    /**
     * LocalDateTime转Timestamp
     *
     * @param localDateTime LocalDateTime
     * @return Timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * Instant转Timestamp
     *
     * @param instant Instant
     * @return Timestamp
     */
    public static Timestamp toTimestamp(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return Timestamp.from(instant);
    }

    /**
     * 时间戳epochMilli转Timestamp
     *
     * @param epochMilli 时间戳
     * @return Timestamp
     */
    public static Timestamp toTimestamp(long epochMilli) {
        return new Timestamp(epochMilli);
    }

    /**
     * Date转YearMonth
     *
     * @param date Date
     * @return YearMonth
     */
    public static YearMonth toYearMonth(Date date) {
        LocalDate localDate = toLocalDate(date);
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    /**
     * LocalDateTime转YearMonth
     *
     * @param localDateTime LocalDateTime
     * @return YearMonth
     */
    public static YearMonth toYearMonth(LocalDateTime localDateTime) {
        LocalDate localDate = toLocalDate(localDateTime);
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    /**
     * LocalDate转YearMonth
     *
     * @param localDate LocalDate
     * @return YearMonth
     */
    public static YearMonth toYearMonth(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    /**
     * Instant转YearMonth
     *
     * @param instant Instant
     * @return YearMonth
     */
    public static YearMonth toYearMonth(Instant instant) {
        LocalDate localDate = toLocalDate(instant);
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    /**
     * ZonedDateTime转YearMonth
     *
     * @param zonedDateTime ZonedDateTime
     * @return YearMonth
     */
    public static YearMonth toYearMonth(ZonedDateTime zonedDateTime) {
        LocalDate localDate = toLocalDate(zonedDateTime);
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    /**
     * Date转ZonedDateTime，时区为系统默认时区
     *
     * @param date Date
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        Objects.requireNonNull(date, "date");
        return Instant.ofEpochMilli(date.getTime()).atZone(DEFAULT_ZONE_ID);
    }

    /**
     * Date转ZonedDateTime
     *
     * @param date   Date
     * @param zoneId 目标时区
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Date date, String zoneId) {
        Objects.requireNonNull(zoneId, "zoneId");
        return toZonedDateTime(date, ZoneId.of(zoneId));
    }

    /**
     * Date转ZonedDateTime
     *
     * @param date Date
     * @param zone 目标时区
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zone) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(zone, "zone");
        return Instant.ofEpochMilli(date.getTime()).atZone(zone);
    }

    /**
     * LocalDateTime转ZonedDateTime，时区为系统默认时区
     *
     * @param localDateTime LocalDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.atZone(DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime转ZonedDateTime，时区为zoneId对应时区
     * 注意，需要保证localDateTime和zoneId是对应的，不然会出现错误
     *
     * @param localDateTime LocalDateTime
     * @param zoneId        LocalDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, String zoneId) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return localDateTime.atZone(ZoneId.of(zoneId));
    }

    /**
     * LocalDate转ZonedDateTime，时区为系统默认时区
     *
     * @param localDate LocalDate
     * @return ZonedDateTime such as 2020-02-19T00:00+08:00[Asia/Shanghai]
     */
    public static ZonedDateTime toZonedDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay().atZone(DEFAULT_ZONE_ID);
    }

    /**
     * LocalTime转ZonedDateTime
     * 以当天的日期+LocalTime组成新的ZonedDateTime，时区为系统默认时区
     *
     * @param localTime LocalTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime).atZone(DEFAULT_ZONE_ID);
    }

    /**
     * Instant转ZonedDateTime，时区为系统默认时区
     *
     * @param instant Instant
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID).atZone(DEFAULT_ZONE_ID);
    }

    /**
     * 时间戳epochMilli毫秒转ZonedDateTime，时区为系统默认时区
     *
     * @param epochMilli 时间戳
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE_ID)
                .atZone(DEFAULT_ZONE_ID);
    }

    /**
     * temporal转ZonedDateTime，时区为系统默认时区
     *
     * @param temporal TemporalAccessor
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal).atZone(DEFAULT_ZONE_ID);
    }

    /**
     * 当天
     *
     * @param localTime 时间
     * @return 当天
     */
    private static LocalDateTime toDay(LocalTime localTime) {
        return LocalDateTime.of(LocalDate.now(), localTime);
    }

    /**
     * 构建秒
     *
     * @param negate      方向
     * @param daysAsSecs  天
     * @param hoursAsSecs 小时
     * @param minsAsSecs  分组
     * @param secs        秒
     * @param nanos       毫秒
     * @return 秒
     */
    private static long createSeconds(boolean negate, long daysAsSecs, long hoursAsSecs, long minsAsSecs, long secs, int nanos) {
        long exact = Math.addExact(daysAsSecs, Math.addExact(hoursAsSecs, Math.addExact(minsAsSecs, secs)));
        return negate ? -1 * exact : exact;
    }

    /**
     * 时间转化
     *
     * @param text       文本
     * @param parsed     表达式
     * @param multiplier 多值
     * @param errorText  错误信息
     * @return 数字
     */
    private static long parseNumber(CharSequence text, String parsed, int multiplier, String errorText) {
        // regex limits to [-+]?[0-9]+
        if (parsed == null) {
            return 0;
        }
        try {
            long val = Long.parseLong(parsed);
            return Math.multiplyExact(val, multiplier);
        } catch (NumberFormatException | ArithmeticException ex) {
            throw (DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex);
        }
    }

    /**
     * Index of sign charaters (i.e. '+' or '-').
     *
     * @param str      要搜索的字符串
     * @param startPos 开始位置
     * @return 第一个符号字符的索引；如果找不到，则返回-1
     */
    private static int indexOfSignChars(String str, int startPos) {
        int idx = indexOf(str, '+', startPos);
        if (idx < 0) {
            idx = indexOf(str, '-', startPos);
        }
        if (idx < 0) {
            idx = indexOf(str, '/', startPos);
        }
        return idx;
    }

    /**
     * 在日期字符串中重新格式化时区。
     *
     * @param str     输入字符串
     * @param signIdx 符号字符的索引位置
     * @return 重新格式化的字符串
     */
    private static String reformatTimezone(String str, int signIdx) {
        String str2 = str;
        if (signIdx >= 0 &&
                signIdx + 5 < str.length() &&
                Character.isDigit(str.charAt(signIdx + 1)) &&
                Character.isDigit(str.charAt(signIdx + 2)) &&
                str.charAt(signIdx + 3) == ':' &&
                Character.isDigit(str.charAt(signIdx + 4)) &&
                Character.isDigit(str.charAt(signIdx + 5))) {
            str2 = str.substring(0, signIdx + 3) + str.substring(signIdx + 4);
        }
        return str2;
    }

    /**
     * 获取 DateFormat
     *
     * @param pattern 表达式
     * @return DateFormat
     * @see DateFormat
     */
    private static DateFormat createDefaultDateFormat(String pattern) {
        return createDateFormat(pattern, null);
    }

    /**
     * 获取 DateFormat
     *
     * @param pattern  表达式
     * @param timeZone 时区
     * @return DateFormat
     * @see DateFormat
     */
    private static DateFormat createDateFormat(String pattern, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (null != timeZone) {
            TimeZone gmt = TimeZone.getTimeZone(timeZone);
            sdf.setTimeZone(gmt);
        }
        sdf.setLenient(true);
        return sdf;
    }

    /**
     * 补毫秒
     *
     * @param time 时间
     * @return 补毫秒
     */
    private static long complementMilliseconds(long time) {
        String timeStr = time + "";
        int length = timeStr.length();
        if (length == MILLISECOND) {
            return time;
        }

        BigDecimal bigDecimal = new BigDecimal(time);
        if (length < MILLISECOND) {
            return bigDecimal.multiply(BigDecimal.TEN.pow(MILLISECOND - length)).longValue();
        }

        return bigDecimal.subtract(BigDecimal.TEN.pow(length - MILLISECOND)).longValue();

    }

    /**
     * 日历
     *
     * @param year   年
     * @param month  月
     * @param day    天
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @param milli  毫秒
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month, int day, int hour, int minute, int second, int milli) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_ZONE_ID));
        c.set(year, month - 1, day, hour, minute, second);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 日历
     *
     * @param year   年
     * @param month  月
     * @param day    天
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month, int day, int hour, int minute, int second) {
        return toCalendar(year, month, day, hour, minute, second, 0);
    }

    /**
     * 日历
     *
     * @param year   年
     * @param month  月
     * @param day    天
     * @param hour   小时
     * @param minute 分钟
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month, int day, int hour, int minute) {
        return toCalendar(year, month, day, hour, minute, 0, 0);
    }

    /**
     * 日历
     *
     * @param year  年
     * @param month 月
     * @param day   天
     * @param hour  小时
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month, int day, int hour) {
        return toCalendar(year, month, day, hour, 0, 0, 0);
    }

    /**
     * 日历
     *
     * @param year  年
     * @param month 月
     * @param day   天
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month, int day) {
        return toCalendar(year, month, day, 0, 0, 0);
    }

    /**
     * 日历
     *
     * @param year  年
     * @param month 月
     * @return 日历
     */
    public static Calendar toCalendar(int year, int month) {
        return toCalendar(year, month, 1, 0, 0, 0);
    }

    /**
     * 日历
     *
     * @param date 日期
     * @return 日历
     */
    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_ZONE_ID));
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 去除所有空格
     * <pre>
     *     trimAllWhitespace("test") = "test"
     *     trimAllWhitespace("test ") = "test"
     *     trimAllWhitespace(" test ") = "test"
     *     trimAllWhitespace(" te st ") = "test"
     *     trimAllWhitespace(null) = null
     * </pre>
     *
     * @param source 原始数据
     * @return
     */
    private static String trimAllWhitespace(String source) {
        if (null == source || source.length() == 0) {
            return source;
        } else {
            int len = source.length();
            StringBuilder sb = new StringBuilder(source.length());
            for (int i = 0; i < len; ++i) {
                char c = source.charAt(i);
                if (!Character.isWhitespace(c)) {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @return 位置
     */
    private static int indexOf(CharSequence str, char searchChar, int start) {
        if (str instanceof String) {
            return ((String) str).indexOf(searchChar, start);
        } else {
            return indexOf(str, searchChar, start, -1);
        }
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @param end        终止位置，如果超过str.length()则默认查找到字符串末尾
     * @return 位置
     */
    private static int indexOf(final CharSequence str, char searchChar, int start, int end) {
        if (null == str) {
            return INDEX_NOT_FOUND;
        }
        final int len = str.length();
        if (start < 0 || start > len) {
            start = 0;
        }
        if (end > len || end < 0) {
            end = len;
        }
        for (int i = start; i < end; i++) {
            if (str.charAt(i) == searchChar) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 获取时长单位简写
     *
     * @param unit 单位
     * @return 单位简写名称
     */
    public static String getShotName(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            default:
                return unit.name().toLowerCase();
        }
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * @return 今年
     */
    public static Date currentDate() {
        return new Date();
    }

    /**
     * @return 今年
     */
    public static int thisYear() {
        return year(currentDate());
    }

    /**
     * 获得年的部分
     *
     * @param date 日期
     * @return 年的部分
     */
    public static int year(Date date) {
        return DateTime.of(date).getYear();
    }

    /**
     * 根据 formatter解析为 ZonedDateTime
     *
     * @param text      待解析字符串
     * @param formatter DateTimeFormatter
     * @return ZonedDateTime
     */
    public static ZonedDateTime parseToZonedDateTime(String text, DateTimeFormatter formatter) {
        return ZonedDateTime.parse(text, formatter);
    }
}
