package com.chua.common.support.log;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.utils.StringUtils;

/**
 * 日志
 *
 * @author CH
 */
@Extension("sys")
public class SysLog implements Log {


    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String message, Throwable e) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String message, Object... args) {
        System.err.println(StringUtils.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        System.out.println(StringUtils.format(message, args));
    }

    @Override
    public void trace(String message, Object... args) {
        System.out.println(StringUtils.format(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        System.out.println(StringUtils.format(message, args));
    }

    @Override
    public void info(String message, Object... args) {
        System.out.println(StringUtils.format(message, args));
    }
}
