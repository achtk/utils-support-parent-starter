package com.chua.common.support.lang.date.format;

import com.chua.common.support.lang.date.type.TimeOfDay;
import com.chua.common.support.lang.date.unit.DateUnit;
import com.chua.common.support.utils.StringUtils;

/**
 * 区间格式化
 *
 * @author CH
 */
public class BetweenFormatter {
    /**
     * 时长毫秒数
     */
    private long betweenMs;
    /**
     * 格式化级别
     */
    private TimeOfDay timeOfDay;
    /**
     * 格式化级别的最大个数
     */
    private final int levelMaxCount;

    /**
     * 构造
     *
     * @param betweenMs 日期间隔
     * @param timeOfDay 级别，按照天、小时、分、秒、毫秒分为5个等级，根据传入等级，格式化到相应级别
     */
    public BetweenFormatter(long betweenMs, TimeOfDay timeOfDay) {
        this(betweenMs, timeOfDay, 0);
    }

    /**
     * 构造
     *
     * @param betweenMs     日期间隔
     * @param timeOfDay     级别，按照天、小时、分、秒、毫秒分为5个等级，根据传入等级，格式化到相应级别
     * @param levelMaxCount 格式化级别的最大个数，假如级别个数为1，但是级别到秒，那只显示一个级别
     */
    public BetweenFormatter(long betweenMs, TimeOfDay timeOfDay, int levelMaxCount) {
        this.betweenMs = betweenMs;
        this.timeOfDay = timeOfDay;
        this.levelMaxCount = levelMaxCount;
    }

    /**
     * 格式化日期间隔输出<br>
     *
     * @return 格式化后的字符串
     */
    public String format() {
        final StringBuilder sb = new StringBuilder();
        if (betweenMs > 0) {
            long day = betweenMs / DateUnit.DAY.getMillis();
            long hour = betweenMs / DateUnit.HOUR.getMillis() - day * 24;
            long minute = betweenMs / DateUnit.MINUTE.getMillis() - day * 24 * 60 - hour * 60;

            final long betweenOfSecond = ((day * 24 + hour) * 60 + minute) * 60;
            long second = betweenMs / DateUnit.SECOND.getMillis() - betweenOfSecond;
            long millisecond = betweenMs - (betweenOfSecond + second) * 1000;

            final int level = this.timeOfDay.ordinal();
            int levelCount = 0;

            if (isLevelCountValid(levelCount) && 0 != day && level >= TimeOfDay.DAY.ordinal()) {
                sb.append(day).append(TimeOfDay.DAY.getName());
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != hour && level >= TimeOfDay.HOUR.ordinal()) {
                sb.append(hour).append(TimeOfDay.HOUR.getName());
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != minute && level >= TimeOfDay.MINUTE.ordinal()) {
                sb.append(minute).append(TimeOfDay.MINUTE.getName());
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != second && level >= TimeOfDay.SECOND.ordinal()) {
                sb.append(second).append(TimeOfDay.SECOND.getName());
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != millisecond && level >= TimeOfDay.MILLISECOND.ordinal()) {
                sb.append(millisecond).append(TimeOfDay.MILLISECOND.getName());
                // levelCount++;
            }
        }

        if (StringUtils.isEmpty(sb)) {
            sb.append(0).append(this.timeOfDay.getName());
        }

        return sb.toString();
    }

    /**
     * 获得 时长毫秒数
     *
     * @return 时长毫秒数
     */
    public long getBetweenMs() {
        return betweenMs;
    }

    /**
     * 设置 时长毫秒数
     *
     * @param betweenMs 时长毫秒数
     */
    public void setBetweenMs(long betweenMs) {
        this.betweenMs = betweenMs;
    }

    /**
     * 获得 格式化级别
     *
     * @return 格式化级别
     */
    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * 设置格式化级别
     *
     * @param timeOfDay 格式化级别
     */
    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }


    @Override
    public String toString() {
        return format();
    }

    /**
     * 等级数量是否有效<br>
     * 有效的定义是：levelMaxCount大于0（被设置），当前等级数量没有超过这个最大值
     *
     * @param levelCount 登记数量
     * @return 是否有效
     */
    private boolean isLevelCountValid(int levelCount) {
        return this.levelMaxCount <= 0 || levelCount < this.levelMaxCount;
    }
}
