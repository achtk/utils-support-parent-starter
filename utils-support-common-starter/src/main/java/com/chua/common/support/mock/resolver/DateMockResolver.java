package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.Mock;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.Preconditions;
import com.chua.common.support.utils.RandomUtils;
import com.chua.common.support.utils.StringUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * 日期
 *
 * @author CH
 */
@Spi("date")
public class DateMockResolver implements MockResolver {
    /**
     * 日期时间格式缓存
     */
    private static final Map<String, DateTimeFormatter> TIME_FORMATTER_MAP = new ConcurrentHashMap<>();

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+8");

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String format = StringUtils.defaultString(mock.formatter(), "yyyy-MM-dd");
        return getValue(mock, format);
    }

    /**
     * 格式化数据
     *
     * @param mock   参数
     * @param format 格式
     * @return 结果
     */
    public String getValue(MockValue mock, String format) {
        if (mock.symbol() == Mock.Symbol.AFTER) {
            try {
                return DateUtils.format(randomFutureLocalDate(DateUtils.toLocalDate(mock.base())), format);
            } catch (ParseException ignored) {
            }
        }

        if (mock.symbol() == Mock.Symbol.BEFORE) {
            try {
                return DateUtils.format(randomPastDate(DateUtils.toLocalDate(mock.base())), format);
            } catch (ParseException ignored) {
            }
        }
        return DateUtils.format(randomPastDate(), format);
    }


    /**
     * 随机日期
     *
     * @param year    年份
     * @param pattern 日期格式
     * @return 随机日期字符串
     */
    public static String randomDate(int year, String pattern) {
        Preconditions.checkArgument(!StringUtils.isEmpty(pattern), "日期格式为空");
        LocalDate date = randomLocalDate(year);
        return date.format(TIME_FORMATTER_MAP.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 随机日期
     *
     * @param year 年份
     * @return 随机日期
     */
    public static Date randomDate(int year) {
        return localDateToDate(randomLocalDate(year));
    }

    /**
     * 随机的LocalDate日期
     *
     * @param year 年份
     * @return 随机的LocalDate日期
     */
    public static LocalDate randomLocalDate(int year) {
        Preconditions.checkArgument(year >= 1970 && year <= 9999, "年份无效");
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        LocalDate begin = LocalDate.of(year, 1, 1);
        return begin.plusDays(RandomUtils.randomInt(0, isLeapYear ? 366 : 365));
    }

    /**
     * 获取特定范围内的随机日期
     *
     * @param beginDate 范围开始(含)
     * @param endDate   范围结束(含)
     * @return 随机日期字符串
     */
    public static LocalDate randomLocalDate(LocalDate beginDate, LocalDate endDate) {
        Preconditions.checkArgument(beginDate != null && endDate != null, "日期范围不能为空");
        Preconditions.checkArgument(beginDate.isBefore(endDate), "日期范围无效");
        long diff = DAYS.between(beginDate, endDate);
        return beginDate.plusDays(RandomUtils.randomLong(0, diff + 1));
    }

    /**
     * 获取特定范围内的随机日期
     *
     * @param beginDate 范围开始(含)
     * @param endDate   范围结束(含)
     * @param pattern   日期格式
     * @return 随机日期字符串
     */
    public static String randomDate(LocalDate beginDate, LocalDate endDate, String pattern) {
        Preconditions.checkArgument(!StringUtils.isEmpty(pattern), "日期格式为空");
        LocalDate date = randomLocalDate(beginDate, endDate);
        return date.format(TIME_FORMATTER_MAP.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 获取特定范围内的随机日期
     *
     * @param beginDate 范围开始(含)
     * @param endDate   范围结束(含)
     * @return 随机日期
     */
    public static Date randomDate(LocalDate beginDate, LocalDate endDate) {
        return localDateToDate(randomLocalDate(beginDate, endDate));
    }

    /**
     * 随机未来日期
     *
     * @param baseDate 基础日期
     * @param pattern  日期格式
     * @return 随机未来日期字符串
     */
    public static String randomFutureDate(LocalDate baseDate, String pattern) {
        Preconditions.checkArgument(!StringUtils.isEmpty(pattern), "日期格式为空");
        LocalDate date = randomFutureLocalDate(baseDate);
        return date.format(TIME_FORMATTER_MAP.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 随机未来日期
     *
     * @param baseDate 基础日期
     * @return 随机未来日期
     */
    public static LocalDate randomFutureLocalDate(LocalDate baseDate) {
        return baseDate.plusDays(RandomUtils.randomLong(1, 99999));
    }

    /**
     * 随机未来日期
     *
     * @param baseDate 基础日期
     * @return 随机未来日期
     */
    public static Date randomFutureDate(LocalDate baseDate) {
        return localDateToDate(randomFutureLocalDate(baseDate));
    }

    /**
     * 随机未来日期(以当天为基准)
     *
     * @param pattern 日期格式
     * @return 随机未来日期字符串
     */
    public static String randomFutureDate(String pattern) {
        return randomFutureDate(LocalDate.now(), pattern);
    }

    /**
     * 随机未来日期(以当天为基准)
     *
     * @return 随机未来日期字符串
     */
    public static Date randomFutureDate() {
        return localDateToDate(randomFutureLocalDate(LocalDate.now()));
    }

    /**
     * 随机以往日期
     *
     * @param baseDate 基础日期
     * @param maxDays  最大日期间隔(天)
     * @param pattern  日期格式
     * @return 随机以往日期字符串
     */
    public static String randomPastDate(LocalDate baseDate, long maxDays, String pattern) {
        Preconditions.checkArgument(!StringUtils.isEmpty(pattern), "日期格式为空");
        LocalDate date = randomPastLocalDate(baseDate, maxDays);
        return date.format(TIME_FORMATTER_MAP.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 随机以往日期
     *
     * @param baseDate 基础日期
     * @param maxDays  最大日期间隔(天)
     * @return 随机以往日期
     */
    public static LocalDate randomPastLocalDate(LocalDate baseDate, long maxDays) {
        Preconditions.checkArgument(maxDays > 1, "最大日期间隔无效");
        return baseDate.plusDays(-1 * RandomUtils.randomLong(1, maxDays + 1));
    }

    /**
     * 随机以往日期
     *
     * @param baseDate 基础日期
     * @param maxDays  最大日期间隔(天)
     * @return 随机以往日期
     */
    public static Date randomPastDate(LocalDate baseDate, long maxDays) {
        return localDateToDate(randomPastLocalDate(baseDate, maxDays));
    }

    /**
     * 随机以往日期(1年内)
     *
     * @param baseDate 基础日期
     * @param pattern  日期格式
     * @return 随机以往日期字符串
     */
    public static String randomPastDate(LocalDate baseDate, String pattern) {
        return randomPastDate(baseDate, 365, pattern);
    }

    /**
     * 随机以往日期(1年内)
     *
     * @param baseDate 基础日期
     * @return 随机以往日期
     */
    public static Date randomPastDate(LocalDate baseDate) {
        return localDateToDate(randomPastLocalDate(baseDate, 365));
    }

    /**
     * 随机以往日期(1年内)
     *
     * @param pattern 日期格式
     * @return 随机以往日期字符串
     */
    public static String randomPastDate(String pattern) {
        return randomPastDate(LocalDate.now(), pattern);
    }

    /**
     * 随机以往日期(1年内)
     *
     * @return 随机以往日期
     */
    public static Date randomPastDate() {
        return randomPastDate(LocalDate.now());
    }

    /**
     * 随机时间
     *
     * @param year       年
     * @param month      月
     * @param dayOfMonth 日
     * @return 随机时间
     */
    public static LocalDateTime randomTime(int year, int month, int dayOfMonth) {
        Preconditions.checkArgument(month >= 1 && month <= 12, "月份错误");
        int hour = RandomUtils.randomInt(0, 24);
        int minute = RandomUtils.randomInt(0, 60);
        int second = RandomUtils.randomInt(0, 60);
        int millisecond = RandomUtils.randomInt(0, 1000);
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, millisecond);
    }

    /**
     * 随机时间
     *
     * @param year       年
     * @param month      月
     * @param dayOfMonth 日
     * @return 随机时间
     */
    public static Date randomDate(int year, int month, int dayOfMonth) {
        return localDateTimeToDate(randomTime(year, month, dayOfMonth));
    }

    /**
     * 过去的随机时间(以当天为基准)
     *
     * @param maxDays 最大日期间隔
     * @return 过去的随机时间
     */
    public static LocalDateTime randomPastTime(int maxDays) {
        Preconditions.checkArgument(maxDays >= 1, "最大日期间隔必须大于0");
        return randomPastTime(LocalDateTime.now(), maxDays * 86400);
    }

    /**
     * 过去的随机时间(以当天为基准)
     *
     * @param maxDays 最大日期间隔
     * @return 过去的随机时间
     */
    public static Date randomPastDate(int maxDays) {
        return localDateTimeToDate(randomPastTime(maxDays));
    }

    /**
     * 过去的随机时间
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒，0或负值表示不限
     * @return 过去的随机时间
     */
    public static LocalDateTime randomPastTime(LocalDateTime base, long maxSeconds) {
        Preconditions.checkArgument(base != null, "基准时间不能为空");
        long second = maxSeconds > 1L ? RandomUtils.randomLong(0L, maxSeconds + 1L) : RandomUtils.randomLong();
        long millisecond = RandomUtils.randomLong(0L, 1000L);
        return base.minus(second, ChronoUnit.SECONDS).minus(millisecond, ChronoUnit.MILLIS);
    }

    /**
     * 过去的随机时间
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒，0或负值表示不限
     * @return 过去的随机时间
     */
    public static Date randomPastDate(LocalDateTime base, long maxSeconds) {
        return localDateTimeToDate(randomPastTime(base, maxSeconds));
    }

    /**
     * 未来的随机时间(以当天为基准)
     *
     * @param maxDays 最大日期间隔
     * @return 未来的随机时间
     */
    public static LocalDateTime randomFutureTime(int maxDays) {
        Preconditions.checkArgument(maxDays >= 1, "最大日期间隔必须大于0");
        return randomFutureTime(LocalDateTime.now(), maxDays * 86400);
    }

    /**
     * 未来的随机时间(以当天为基准)
     *
     * @param maxDays 最大日期间隔
     * @return 未来的随机时间
     */
    public static Date randomFutureDate(int maxDays) {
        return localDateTimeToDate(randomFutureTime(maxDays));
    }

    /**
     * 未来的随机时间
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒，0或负值表示不限
     * @return 未来的随机时间
     */
    public static LocalDateTime randomFutureTime(LocalDateTime base, long maxSeconds) {
        Preconditions.checkArgument(base != null, "基准时间不能为空");
        long second = maxSeconds > 1L ? RandomUtils.randomLong(0L, maxSeconds + 1L) : RandomUtils.randomLong();
        long millisecond = RandomUtils.randomLong(0L, 1000L);
        return base.plus(second, ChronoUnit.SECONDS).plus(millisecond, ChronoUnit.MILLIS);
    }

    /**
     * 未来的随机时间
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒，0或负值表示不限
     * @return 未来的随机时间
     */
    public static Date randomFutureDate(LocalDateTime base, long maxSeconds) {
        return localDateTimeToDate(randomFutureTime(base, maxSeconds));
    }

    /**
     * 随机时间戳(毫秒)
     *
     * @param begin 开始时间(含)
     * @param end   结束时间(不含)
     * @return 随机时间戳(毫秒)
     */
    public static long randomTimestamp(LocalDateTime begin, LocalDateTime end) {
        Preconditions.checkArgument(begin != null && end != null, "开始时间和结束时间不能为空");
        Preconditions.checkArgument(begin.isBefore(end), "开始时间必须早于结束时间");
        Duration duration = Duration.between(begin, end);
        long millis = duration.toMillis();
        return begin.toInstant(ZONE_OFFSET).toEpochMilli() + RandomUtils.randomLong(0, millis);
    }

    /**
     * 获取未来的随机时间戳(毫秒)
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒
     * @return 未来的随机时间戳(毫秒)
     */
    public static long randomFutureTimestamp(LocalDateTime base, long maxSeconds) {
        return randomTimestamp(base, maxSeconds);
    }

    /**
     * 获取过去的随机时间戳(毫秒)
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒
     * @return 过去的随机时间戳(毫秒)
     */
    public static long randomPastTimestamp(LocalDateTime base, long maxSeconds) {
        return randomTimestamp(base, maxSeconds > 0 ? -1 * maxSeconds : maxSeconds);
    }

    /**
     * 获取某一天内的随机时间戳(毫秒)
     *
     * @param date 日期
     * @return 随机时间戳(毫秒)
     */
    public static long randomTimestamp(LocalDate date) {
        Preconditions.checkArgument(date != null, "日期不能为空");
        LocalDateTime begin = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return randomTimestamp(begin, end);
    }

    /**
     * 获取一个随机的时区名称
     *
     * @return 随机的时区名称
     */
    public static String randomTimezoneName() {
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        Object[] array = zoneIds.toArray();
        return Objects.toString(array[RandomUtils.randomInt(0, array.length)]);
    }

    /**
     * 随机时间戳
     *
     * @param base       基准时间
     * @param maxSeconds 最大相差秒
     * @return 随机时间戳
     */
    private static long randomTimestamp(LocalDateTime base, long maxSeconds) {
        Preconditions.checkArgument(base != null, "基准时间不能为空");
        Preconditions.checkArgument(Math.abs(maxSeconds) > 1, "相差秒数必须大于1");
        long diff = maxSeconds > 0 ? RandomUtils.randomLong(1, maxSeconds * 1000 + 1) : -1 * (RandomUtils.randomLong(1, Math.abs(maxSeconds) * 1000 + 1));
        return base.toInstant(ZONE_OFFSET).toEpochMilli() + diff;
    }

    /**
     * 转换LocalDate到Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    private static Date localDateToDate(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 转换LocalDateTime到Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }
}
