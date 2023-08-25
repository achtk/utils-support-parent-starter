package com.chua.example.date;

import com.chua.common.support.lang.date.DateRangeTime;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.lang.date.Times;
import com.chua.common.support.lang.date.lunar.LunarTime;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import static com.chua.common.support.lang.date.DateTimeType.DAY_OF_MONTH;
import static java.time.DayOfWeek.MONDAY;

/**
 * @author CH
 * @since 2022-03-18
 */
@Slf4j
public class DateExample {

    public static void main(String[] args) throws ParseException {

        LocalDateTime localDateTime = DateTime.now().withHour(17).withZeroMinute().toLocalDateTime();
        DateTime dateTime = DateTime.now();
        System.out.println(dateTime.duration(localDateTime));
        DateRangeTime dateRangeTime = DateRangeTime.greaterThan(LocalDateTime.now());
        LocalDateTime time1 = DateTime.now().plusMinutes(1).toLocalDateTime();
        log.info("时间区间{}是否包含:{} -> {}", new Object[]{dateRangeTime.toString(), time1 + "", dateRangeTime.contains(time1) + ""});

        log.info("unix => {}", dateTime.toUnixTimestamp());
        log.info("当前时间 => {}", dateTime.getStringDate());
        log.info("当前{}", dateTime.getDayOfWeekName());
        log.info("Sat Feb 04 03:44:21 CST 2023 => yyyy-MM-dd HH:mm:ss({})", DateTime.of("Sat Feb 04 03:44:21 CST 2023").toStandard());
        log.info("-1d -> {}", DateTime.of("-1d").toString("yyyy-MM-dd HH:mm:ss"));
        log.info("获取{} ~ 今天的周期", DateTime.of("2023-1-1").asWeek(DateTime.now()));
        log.info("当前{}号", dateTime.getDayOfMonth());
        log.info("当前一个月: {}", dateTime.getCurrentMonth());
        log.info("前一天{}号", dateTime.minusDays(1).getDayOfMonth());
        log.info("后一天{}号", dateTime.plusDays(1).getDayOfMonth());
        log.info("获取本周一周时间: {}", dateTime.asRangeWeek());
        log.info("获取本周第一天时间: {}", dateTime.firstDayOfWeek());
        log.info("获取本周最后一天时间: {}", dateTime.lastDayOfWeek());
        log.info("距离2022-05-01多少天: {}", dateTime.betweenOfLocalDate(DateTime.of("2022-05-01")));
        log.info("获取当天12:30:01: {}", dateTime.withHour(12).withMinute(30).withSecond(1).toLocalDateTime());
        log.info("获取当天12:30:01: {}", dateTime.withTime("12:30:01").toLocalDateTime());
        log.info("获取当天12:30:01: {}", dateTime.withTime("12h30m1s").toLocalDateTime());
        log.info("距离2021-05-01每天8:00, 11:00: {}", dateTime.betweenOfLocalDateTime(DateTime.of("2021-05-01"), "08:00", "11:00"));
        log.info("判断当前时间是否是8:00 ~ 11:00: {}", dateTime.isRange("8:00", "11:00"));
        log.info("判断当前时间是否是8:00: {}", dateTime.isTimeOfMinEquals("8:00"));
        log.info("获取上2周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY, 2).withFirstTimeOfDay());
        log.info("获取下2周 -> MONDAY: {}", dateTime.afterDayOfCalendar(MONDAY, 2).withFirstTimeOfDay());
        log.info("获取上周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY).withFirstTimeOfDay());
        log.info("获取下周 -> MONDAY: {}", dateTime.afterDayOfCalendar(MONDAY).withFirstTimeOfDay());
        log.info("获取本周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY).withFirstTimeOfDay());
        log.info("获取本周 -> MONDAY: {}", dateTime.getDayOfCalendar(MONDAY).withFirstTimeOfDay());
        log.info("获取后面5天时间: {}", dateTime.afterDay(5));
        log.info("获取前面5天时间: {}", dateTime.beforeDay(5));
        log.info("获取前7天时间: {}", dateTime.asRangeDayUntil(-7));
        log.info("获取后7天时间: {}", dateTime.asRangeDayUntil(7));
        log.info("获取两个时间之间的时间段: {}", dateTime.asRange(dateTime.firstDayOfCurrentMonth(), new Date()));
        log.info("获取两个时间之间天数: {}", dateTime.betweenOfDay(dateTime.firstDayOfWeek(), new Date()));
        log.info("获取2020-12-21 12:12:12 => {}", dateTime.withYear(2020).withMonth(12).withDayOfMonth(21).withHour(12).withMinute(12).withSecond(12).toString("yyyy-MM-dd HH:mm:ss"));
        log.info("获取2020-12-01 00:00:00 => {}", dateTime.withYear(2020).withMonth(12).withFirstDayOfMonth().toString("yyyy-MM-dd HH:mm:ss"));
        log.info("获取2020-12-31 23:59:59 => {}", dateTime.withYear(2020).withMonth(12).withLastDayOfMonth().toString("yyyy-MM-dd HH:mm:ss"));

        log.info("获取多个时间点: {}", dateTime.fullPoint(DAY_OF_MONTH, 1, 3, 4, 5));
        log.info("获取多个时间区间: {}", dateTime.fullRange(DAY_OF_MONTH, 1, 3, 4, 5));

        log.info("两个时间差: {}", Times.betweenOfDay("2021-12-29", "2021-12-1"));

        int time = 10;
        while (time > 0) {
            log.info("格式化持续时间: {}", Times.betweenOfFormat(time * 1000));
            ThreadUtils.sleepSecondsQuietly(1);
            time--;
        }

        LunarTime lunarTime = dateTime.toLunarTime();
        System.out.println("农历年: " + lunarTime.getYearInGanZhi());
        System.out.println("生肖年: " + lunarTime.getYearShengXiao());
        System.out.println("干支月: " + lunarTime.getMonthInGanZhi());
        System.out.println("农历月: " + lunarTime.getMonthInChinese());
        System.out.println("生肖月: " + lunarTime.getMonthShengXiao());
        System.out.println("当前节气: " + lunarTime.getJieQi());
        System.out.println("下个节气: " + lunarTime.getNextJieQi() + " (" + lunarTime.getNextJieQi().getSolar().toString() + ")");
        System.out.println("星座: " + lunarTime.getSolar().getXingZuo());
        System.out.println(lunarTime.toFullString());
        System.out.println(lunarTime.getSolar().toFullString());

        Duration duration = Duration.ofMillis(11 * 61 * 1000);

    }
}
