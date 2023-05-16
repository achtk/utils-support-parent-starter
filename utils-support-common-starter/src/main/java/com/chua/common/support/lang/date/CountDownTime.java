package com.chua.common.support.lang.date;

import com.chua.common.support.log.Log;
import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 倒计时计数器
 *
 * @author CH
 * @since 2022-02-18
 */
public class CountDownTime implements Runnable, AutoCloseable {
    private static final Log log = Log.getLogger(CountDownTime.class);

    private final long time;
    private final Thread task;
    private final Consumer<Boolean> endConsumer;
    private long day = 0;
    private long hour = 0;
    private long minute = 0;
    private long second = 0;
    private boolean dayNotAlready = false;
    private boolean hourNotAlready = false;
    private boolean minuteNotAlready = false;
    private boolean secondNotAlready = false;
    private ScheduledExecutorService timer;

    /**
     * 通过时间初始化
     *
     * @param time 时间
     */
    public CountDownTime(long time) {
        this(time, null);
    }

    /**
     * 通过时间初始化
     *
     * @param time 时间
     */
    public CountDownTime(long time, Consumer<Boolean> endConsumer) {
        this.endConsumer = endConsumer;
        this.initData(time);
        this.time = time;
        this.task = ThreadUtils.newThread(this);
        this.task.start();
    }

    @Override
    public void close() throws Exception {
        this.secondNotAlready = false;
        timer.shutdownNow();
        if (null != task) {
            task.interrupt();
        }

        if (null != endConsumer) {
            endConsumer.accept(true);
        }
    }


    /**
     * 是否结束
     *
     * @return 是否结束
     */
    public boolean isEnd() {
        return !secondNotAlready;
    }

    @Override
    public void run() {
        this.timer = ThreadUtils.newScheduledThreadPoolExecutor(1);
        timer.schedule(() -> {
            if (secondNotAlready) {
                startCount();
            } else {
                try {
                    close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 初始化赋值
     *
     * @param totalSeconds 秒
     */
    private void initData(long totalSeconds) {
        resetData();
        int y = 60, i = 24;
        if (totalSeconds > 0) {
            secondNotAlready = true;
            second = totalSeconds;
            if (second >= y) {
                minuteNotAlready = true;
                minute = second / 60;
                second = second % 60;
                if (minute >= y) {
                    hourNotAlready = true;
                    hour = minute / 60;
                    minute = minute % 60;
                    if (hour > i) {
                        dayNotAlready = true;
                        day = hour / 24;
                        hour = hour % 24;
                    }
                }
            }
        }
        log.info("初始格式化后——>{}天{}小时{}分钟{}秒", day, hour, minute, second);
    }

    /**
     * 计算各个值的变动
     */
    public void startCount() {
        if (secondNotAlready) {
            if (second > 0) {
                second--;
                if (second == 0 && !minuteNotAlready) {
                    secondNotAlready = false;
                }
            } else {
                if (minuteNotAlready) {
                    if (minute > 0) {
                        minute--;
                        second = 59;
                        if (minute == 0 && !hourNotAlready) {
                            minuteNotAlready = false;
                        }
                    } else {
                        if (hourNotAlready) {
                            if (hour > 0) {
                                hour--;
                                minute = 59;
                                second = 59;
                                if (hour == 0 && !dayNotAlready) {
                                    hourNotAlready = false;
                                }
                            } else {
                                if (dayNotAlready) {
                                    day--;
                                    hour = 23;
                                    minute = 59;
                                    second = 59;
                                    if (day == 0) {
                                        dayNotAlready = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("距离截止日期还有——>{}天{}小时{}分钟{}秒", day, hour, minute, second);
    }

    /**
     * 重置
     */
    private void resetData() {
        day = 0;
        hour = 0;
        minute = 0;
        second = 0;
        dayNotAlready = false;
        hourNotAlready = false;
        minuteNotAlready = false;
        secondNotAlready = false;
    }
}
