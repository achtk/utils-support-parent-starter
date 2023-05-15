package com.chua.common.support.log;


import com.chua.common.support.spi.ServiceProvider;

/**
 * 日志
 *
 * @author CH
 */
public interface Log {
    /**
     * 初始化
     *
     * @param type 联系
     * @return log
     */
    @SuppressWarnings("ALL")
    public static Log getLogger(Class<?> type) {
        String impl = System.getProperty("global.logger.impl", "slf4j").toUpperCase();
        return getLogger(impl, type);
    }

    /**
     * 初始化
     *
     * @param impl 实现
     * @param type 联系
     * @return log
     */
    public static Log getLogger(String impl, Class<?> type) {
        switch (impl) {
            case "NOLOG":
                return new NoSysLog();
            case "SYS":
                return new SysLog();
            default:
                return new Slf4jLog();
        }
    }

    /**
     * 是否debug
     *
     * @return 是否debug
     */
    boolean isDebugEnabled();

    /**
     * 是否trace
     *
     * @return 是否trace
     */
    boolean isTraceEnabled();

    /**
     * error
     *
     * @param message msg
     * @param e       e
     */
    void error(String message, Throwable e);

    /**
     * error
     *
     * @param message msg
     * @param args    参数
     */
    void error(String message, Object... args);

    /**
     * debug
     *
     * @param message msg
     * @param args    参数
     */
    void debug(String message, Object... args);

    /**
     * trace
     *
     * @param message msg
     * @param args    参数
     */
    void trace(String message, Object... args);

    /**
     * warn
     *
     * @param message msg
     * @param args    参数
     */
    void warn(String message, Object... args);

    /**
     * warn
     *
     * @param message msg
     * @param args    参数
     */
    void info(String message, Object... args);
}
