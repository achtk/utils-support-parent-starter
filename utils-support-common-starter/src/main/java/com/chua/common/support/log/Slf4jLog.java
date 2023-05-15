package com.chua.common.support.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志
 *
 * @author CH
 */
public class Slf4jLog implements Log {

    private Logger logger = LoggerFactory.getLogger(Slf4jLog.class);

    public Slf4jLog() {
    }

    public Slf4jLog(Class<?> type) {
        this.logger = LoggerFactory.getLogger(type);
    }


    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void trace(String message, Object... args) {
        logger.trace(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }
}
