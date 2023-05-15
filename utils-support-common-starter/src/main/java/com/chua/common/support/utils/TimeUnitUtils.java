package com.chua.common.support.utils;

import java.util.concurrent.TimeUnit;

/**
 * 时间工具
 *
 * @author CH
 */
public class TimeUnitUtils {
    /**
     * 转化单位
     *
     * @param unit 单位
     * @return 单位
     */
    public static String abbreviate(TimeUnit unit) {
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
            case DAYS:
                return "d";
            default:
                throw new AssertionError();
        }
    }

    /**
     * 格式化时间
     *
     * @param mill 时间
     * @return 格式化时间
     */
    public static String format(long mill, TimeUnit timeUnit) {
        return formatTime(mill, timeUnit) + " " + abbreviate(timeUnit);
    }

    /**
     * 格式化时间
     *
     * @param mill 时间
     * @return 格式化时间
     */
    public static long formatTime(long mill, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.DAYS) {
            return TimeUnit.MILLISECONDS.toDays(mill);
        }

        if (timeUnit == TimeUnit.HOURS) {
            return TimeUnit.MILLISECONDS.toHours(mill);
        }

        if (timeUnit == TimeUnit.MINUTES) {
            return TimeUnit.MILLISECONDS.toMinutes(mill);
        }

        if (timeUnit == TimeUnit.SECONDS) {
            return TimeUnit.MILLISECONDS.toSeconds(mill);
        }

        return mill;
    }

    /**
     * 格式化时间
     *
     * @param mill 时间
     * @return 格式化时间
     */
    public static String format(long mill) {
        StringBuilder rs = new StringBuilder();
        long convert = formatTime(mill, TimeUnit.DAYS);
        if (convert > 0) {
            rs.append(convert);
            long formatTime = formatTime(mill, TimeUnit.HOURS);
            if (formatTime > 0) {
                rs.append(".").append(formatTime);
            }
            rs.append(" ").append(abbreviate(TimeUnit.DAYS));
            return rs.toString();
        }

        convert = formatTime(mill, TimeUnit.HOURS);
        if (convert > 0) {
            rs.append(convert);
            long formatTime = formatTime(mill, TimeUnit.MINUTES);
            if (formatTime > 0) {
                rs.append(".").append(formatTime);
            }
            rs.append(" ").append(abbreviate(TimeUnit.HOURS));
            return rs.toString();
        }


        convert = formatTime(mill, TimeUnit.MINUTES);
        if (convert > 0) {
            rs.append(convert);
            long formatTime = formatTime(mill, TimeUnit.SECONDS);
            if (formatTime > 0) {
                rs.append(".").append(formatTime);
            }
            rs.append(" ").append(abbreviate(TimeUnit.MINUTES));
            return rs.toString();
        }

        convert = formatTime(mill, TimeUnit.SECONDS);
        if (convert > 0) {
            rs.append(convert);
            long formatTime = formatTime(mill, TimeUnit.MILLISECONDS);
            if (formatTime > 0) {
                rs.append(".").append(formatTime);
            }
            rs.append(" ").append(abbreviate(TimeUnit.SECONDS));
            return rs.toString();
        }


        convert = formatTime(mill, TimeUnit.MILLISECONDS);
        rs.append(convert).append(" ").append(abbreviate(TimeUnit.MILLISECONDS));
        return rs.toString();

    }
}
