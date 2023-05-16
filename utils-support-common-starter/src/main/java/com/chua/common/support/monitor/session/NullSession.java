package com.chua.common.support.monitor.session;

import java.io.Serializable;
import java.util.List;

/**
 * 空会话
 *
 * @author CH
 */
public class NullSession implements Session {

    public static final Session EMPTY = new NullSession();

    @Override
    public List<Serializable[]> getBeforeData() {
        return null;
    }

    @Override
    public List<Serializable[]> getModifyData() {
        return null;
    }

    @Override
    public String root() {
        return null;
    }

    @Override
    public String change() {
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }
}
