package com.chua.common.support.log;

/**
 * 日志
 *
 * @author CH
 */
public class NoSysLog implements Log {


    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String message, Throwable e) {
    }

    @Override
    public void error(String message, Object... args) {
    }

    @Override
    public void debug(String message, Object... args) {
    }

    @Override
    public void trace(String message, Object... args) {
    }

    @Override
    public void warn(String message, Object... args) {
    }

    @Override
    public void info(String message, Object... args) {
    }
}
