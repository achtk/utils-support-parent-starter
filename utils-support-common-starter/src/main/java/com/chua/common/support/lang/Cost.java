package com.chua.common.support.lang;

import com.chua.common.support.constant.Level;
import com.chua.common.support.log.Log;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_N;

/**
 * 计算耗时<br>
 *
 * @author CH
 * @version 1.0.0
 */
public interface Cost {

    /**
     * 停止计时，并返回组装计时结果
     *
     * @param args 参数
     * @return 返回组装计时结果
     */
    String console(Object... args);

    /**
     * 停止计时，返回计时结果
     *
     * @return 返回计时结果
     */
    long stop();

    /**
     * 毫秒
     *
     * @param unit   单位
     * @param format 输出格式
     * @param enable
     * @return this
     */
    static Cost cost(TimeUnit unit, String format, boolean enable) {
        return new CostImpl(Level.INFO, unit, format, enable);
    }

    /**
     * 毫秒
     *
     * @return this
     */
    static Cost cost() {
        return cost(TimeUnit.MILLISECONDS);
    }

    /**
     * 毫秒
     *
     * @param unit 单位
     * @return this
     */
    static Cost cost(TimeUnit unit) {
        return cost(unit, null, true);
    }

    /**
     * 毫秒
     *
     * @param format 输出日志
     * @return this
     */
    static Cost cost(String format) {
        return cost(TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 构建Debug模式
     *
     * @param format 信息
     * @return this
     */
    static Cost debug(String format) {
        return new CostImpl(Level.DEBUG, TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 构建error模式
     *
     * @param format 信息
     * @return this
     */
    static Cost error(String format) {
        return new CostImpl(Level.ERROR, TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 构建info模式
     *
     * @param format 信息
     * @return this
     */
    static Cost info(String format) {
        return new CostImpl(Level.INFO, TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 毫秒
     *
     * @return this
     */
    static Cost mill() {
        return cost(TimeUnit.MILLISECONDS, null, true);
    }

    /**
     * 毫秒
     *
     * @param format 输出格式
     * @return this
     */
    static Cost mill(String format) {
        return cost(TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 毫秒
     *
     * @param enable 是否开启
     * @param format 输出格式
     * @return this
     */
    static Cost enable(boolean enable, String format) {
        return cost(TimeUnit.MILLISECONDS, format, enable);
    }

    /**
     * 构建trace模式
     *
     * @param format 信息
     * @return this
     */
    static Cost trace(String format) {
        return new CostImpl(Level.TRACE, TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 构建error模式
     *
     * @param format 信息
     * @return this
     */
    static Cost warn(String format) {
        return new CostImpl(Level.WARN, TimeUnit.MILLISECONDS, format, true);
    }

    /**
     * 实现
     */
    class CostImpl implements Cost {

        private static final String TIME = "耗时";
        private final boolean enable;
        private TimeUnit unit;
        private String format;
        private Level level;
        private StopWatch stopwatch;
        final Log log;

        public CostImpl(Level level, TimeUnit unit, String format, boolean enable) {
            this.enable = enable;
            log = Log.getLogger(Cost.class);
            if (!enable) {
                return;
            }
            this.level = level;
            if (canStopwatch()) {
                this.stopwatch = StopWatch.createStarted();
            }
            this.unit = unit;
            this.format = Optional.ofNullable(format).orElse("处理任务");
        }

        @Override
        public String console(Object... args) {
            if (!enable) {
                return "";
            }
            String newFormat = this.format;
            if (null != stopwatch) {
                stop();
                if (this.format.trim().endsWith(TIME)) {
                    newFormat = this.format + ": " + stopwatch.toString();
                } else {
                    newFormat = this.format + " " + TIME + ": " + stopwatch.toString();
                }
            }
            for (String item : newFormat.split(SYMBOL_N)) {
                slf4jLog(item, args);
            }
            return newFormat;
        }

        @Override
        public long stop() {
            if (!enable) {
                return -1;
            }

            if (null != stopwatch && stopwatch.isRunning()) {
                stopwatch.stop();
            }
            return null != stopwatch ? 0 : -1L;
        }

        /**
         * 输出日志
         *
         * @param newFormat 格式
         * @param args      参数
         */
        private void slf4jLog(String newFormat, Object[] args) {
            if (!enable) {
                return;
            }

            log.log(level, newFormat, args);
        }

        /**
         * 是否启动监视器
         *
         * @return 是否启动监视器
         */
        private boolean canStopwatch() {
            return !enable;
        }
    }
}
