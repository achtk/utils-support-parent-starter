package com.chua.common.support.lang;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.date.DateUtils;
import com.chua.common.support.span.Span;
import com.chua.common.support.utils.StringUtils;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * watch
 *
 * @author spring
 */
public class StopWatch {

    private static final String DEFAULT_LINE = "\t";
    private static final String LINE = DEFAULT_LINE + DEFAULT_LINE;
    private static final String LINE2 = DEFAULT_LINE + DEFAULT_LINE + DEFAULT_LINE + DEFAULT_LINE;
    /**
     * Identifier of this {@code StopWatch}.
     * <p>Handy when we have output from multiple stop watches and need to
     * distinguish between them in log or console output.
     */
    private final String id;

    private boolean keepTaskList = true;

    @Setter
    private List<TaskInfo> taskList = new ArrayList<>(1);

    /**
     * Start time of the current task.
     */
    private long startTimeNanos;

    /**
     * Name of the current task.
     */
    private String currentTaskName;

    @Setter
    private TaskInfo lastTaskInfo;

    @Setter
    private int taskCount;

    /**
     * Total running time.
     */
    @Setter
    private long totalTimeNanos;

    /**
     * 创建并开启
     *
     * @return this
     */
    public static StopWatch createStarted() {
        return new StopWatch();
    }

    /**
     * 创建并开启
     *
     * @param id id
     * @return this
     */
    public static StopWatch createStarted(String id) {
        return new StopWatch(id);
    }

    /**
     * Construct a new {@code StopWatch}.
     * <p>Does not start any task.
     */
    private StopWatch() {
        this("");
    }

    /**
     * Construct a new {@code StopWatch} with the given ID.
     * <p>The ID is handy when we have output from multiple stop watches and need
     * to distinguish between them.
     * <p>Does not start any task.
     *
     * @param id identifier for this stop watch
     */
    public StopWatch(String id) {
        this.id = id;
    }


    /**
     * Get the ID of this {@code StopWatch}, as specified on construction.
     *
     * @return the ID (empty String by default)
     * @see #StopWatch(String)
     * @since 4.2.2
     */
    public String getId() {
        return this.id;
    }

    /**
     * Configure whether the {@link TaskInfo} array is built over time.
     * <p>Set this to {@code false} when using a {@code StopWatch} for millions
     * of intervals; otherwise, the {@code TaskInfo} structure will consume
     * excessive memory.
     * <p>Default is {@code true}.
     */
    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }


    /**
     * Start an unnamed task.
     * <p>The results are undefined if {@link #stop()} or timing methods are
     * called without invoking this method first.
     *
     * @see #start(String)
     * @see #stop()
     */
    public void start() throws IllegalStateException {
        start("");
    }

    /**
     * Start a named task.
     * <p>The results are undefined if {@link #stop()} or timing methods are
     * called without invoking this method first.
     *
     * @param taskName the name of the task to start
     * @see #start()
     * @see #stop()
     */
    public void start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }
        this.currentTaskName = taskName;
        this.startTimeNanos = System.nanoTime();
    }

    /**
     * Stop the current task.
     * <p>The results are undefined if timing methods are called without invoking
     * at least one pair of {@code start()} / {@code stop()} methods.
     *
     * @see #start()
     * @see #start(String)
     */
    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        long lastTime = System.nanoTime() - this.startTimeNanos;
        this.totalTimeNanos += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime, "");
        if (this.keepTaskList) {
            this.taskList.add(this.lastTaskInfo);
        }
        ++this.taskCount;
        this.currentTaskName = null;
    }

    /**
     * 添加任务
     *
     * @param span span
     */
    public void addTask(Span span) {
        long lastTime = span.getOutTimeNanos() - span.getEntryTimeNanos();
        this.totalTimeNanos += lastTime;
        this.lastTaskInfo = new TaskInfo(span.getTitle(), lastTime, span.getMethod());
        if (this.keepTaskList) {
            this.taskList.add(this.lastTaskInfo);
        }
        ++this.taskCount;
        this.currentTaskName = null;
    }

    /**
     * Determine whether this {@code StopWatch} is currently running.
     *
     * @see #currentTaskName()
     */
    public boolean isRunning() {
        return (this.currentTaskName != null);
    }

    /**
     * Get the name of the currently running task, if any.
     *
     * @see #isRunning()
     * @since 4.2.2
     */
    public String currentTaskName() {
        return this.currentTaskName;
    }

    /**
     * Get the time taken by the last task in nanoseconds.
     *
     * @see #getLastTaskTimeMillis()
     * @since 5.2
     */
    public long getLastTaskTimeNanos() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeNanos();
    }

    /**
     * Get the time taken by the last task in milliseconds.
     *
     * @see #getLastTaskTimeNanos()
     */
    public long getLastTaskTimeMillis() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeMillis();
    }

    /**
     * Get the name of the last task.
     */
    public String getLastTaskName() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task name");
        }
        return this.lastTaskInfo.getTaskName();
    }

    /**
     * Get the last task as a {@link TaskInfo} object.
     */
    public TaskInfo getLastTaskInfo() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }
        return this.lastTaskInfo;
    }


    /**
     * Get the total time in nanoseconds for all tasks.
     *
     * @see #getTotalTimeMillis()
     * @see #getTotalTimeSeconds()
     * @since 5.2
     */
    public long getTotalTimeNanos() {
        return this.totalTimeNanos;
    }

    /**
     * Get the total time in milliseconds for all tasks.
     *
     * @see #getTotalTimeNanos()
     * @see #getTotalTimeSeconds()
     */
    public long getTotalTimeMillis() {
        return nanosToMillis(this.totalTimeNanos);
    }

    /**
     * Get the total time in seconds for all tasks.
     *
     * @see #getTotalTimeNanos()
     * @see #getTotalTimeMillis()
     */
    public double getTotalTimeSeconds() {
        return nanosToSeconds(this.totalTimeNanos);
    }

    /**
     * Get the number of tasks timed.
     */
    public int getTaskCount() {
        return this.taskCount;
    }

    /**
     * Get an array of the data for tasks performed.
     */
    public TaskInfo[] getTaskInfo() {
        if (!this.keepTaskList) {
            throw new UnsupportedOperationException("Task info is not being kept!");
        }
        return this.taskList.toArray(new TaskInfo[0]);
    }


    /**
     * Get a short description of the total running time.
     */
    public String shortSummary() {
        return shortSummary(null);
    }

    /**
     * Get a short description of the total running time.
     *
     * @param unit unit
     */
    public String shortSummary(TimeUnit unit) {
        if (null == unit) {
            unit = TimeUnit.NANOSECONDS;
        }
        return StringUtils.format("StopWatch '{}': running time = {} {}",
                this.id, getTotal(unit), DateUtils.getShotName(unit));
    }

    /**
     * 获取所有任务的总花费时间
     *
     * @param unit 时间单位，{@code null}表示默认{@link TimeUnit#NANOSECONDS}
     * @return 花费时间
     * @since 5.7.16
     */
    public long getTotal(TimeUnit unit) {
        return unit.convert(this.totalTimeNanos, TimeUnit.NANOSECONDS);
    }

    static String formatCompact4Digits(double value) {
        return String.format(Locale.ROOT, "%.4g", value);
    }

    private static TimeUnit chooseUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    private static String abbreviate(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "\u03bcs";
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
     * 生成所有任务的一个任务花费时间表
     *
     * @param unit 时间单位，{@code null}则默认{@link TimeUnit#NANOSECONDS} 纳秒
     * @return 任务时间表
     */
    public String prettyPrint(TimeUnit unit) {
        if (null == unit) {
            unit = TimeUnit.NANOSECONDS;
        }

        final StringBuilder sb = new StringBuilder(shortSummary(unit));
        sb.append(Projects.getLineSeparator());
        if (this.taskList.isEmpty()) {
            sb.append("No task info kept");
        } else {
            sb.append("---------------------------------------------------------------------------------").append(Projects.getLineSeparator());
            sb.append(DateUtils.getShotName(unit))
                    .append("").append(LINE2)
                    .append("%").append(LINE2).append("    ")
                    .append("Task name").append(LINE2).append("method")
                    .append(Projects.getLineSeparator());
            sb.append("---------------------------------------------------------------------------------").append(Projects.getLineSeparator());

            final NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(9);
            nf.setGroupingUsed(false);

            final NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(2);
            pf.setGroupingUsed(false);

            for (TaskInfo task : getTaskInfo()) {
                sb.append(nf.format(task.getTime(unit))).append(LINE);
                sb.append(pf.format((double) task.getTimeNanos() / getTotalTimeNanos())).append("  ").append(LINE).append(LINE);
                sb.append(task.getTaskName()).append(LINE).append(LINE);
                sb.append(task.getMethod()).append(Projects.getLineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Generate a string with a table describing all tasks performed.
     * <p>For custom reporting, call {@link #getTaskInfo()} and use the task info
     * directly.
     */
    public String prettyPrint() {
        return prettyPrint(null);
    }


    /**
     * Generate an informative string describing all tasks performed
     * <p>For custom reporting, call {@link #getTaskInfo()} and use the task info
     * directly.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.keepTaskList) {
            for (TaskInfo task : getTaskInfo()) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos()).append(" ns");
                long percent = Math.round(100.0 * task.getTimeNanos() / getTotalTimeNanos());
                sb.append(" = ").append(percent).append("%");
            }
        } else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }


    private static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    private static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }


    /**
     * Nested class to hold data about one task executed within the {@code StopWatch}.
     */
    public static final class TaskInfo {

        private final String taskName;

        private final long timeNanos;
        private final String method;

        public TaskInfo(String taskName, long timeNanos, String method) {
            this.taskName = taskName;
            this.timeNanos = timeNanos;
            this.method = method;
        }


        /**
         * 获取指定单位的任务花费时间
         *
         * @param unit 单位
         * @return 任务花费时间
         */
        public long getTime(TimeUnit unit) {
            return unit.convert(this.timeNanos, TimeUnit.NANOSECONDS);
        }

        /**
         * Get the name of this task.
         */
        public String getTaskName() {
            return this.taskName;
        }

        public String getMethod() {
            return method;
        }

        /**
         * Get the time in nanoseconds this task took.
         *
         * @see #getTimeMillis()
         * @see #getTimeSeconds()
         * @since 5.2
         */
        public long getTimeNanos() {
            return this.timeNanos;
        }

        /**
         * Get the time in milliseconds this task took.
         *
         * @see #getTimeNanos()
         * @see #getTimeSeconds()
         */
        public long getTimeMillis() {
            return nanosToMillis(this.timeNanos);
        }

        /**
         * Get the time in seconds this task took.
         *
         * @see #getTimeMillis()
         * @see #getTimeNanos()
         */
        public double getTimeSeconds() {
            return nanosToSeconds(this.timeNanos);
        }

    }

}
