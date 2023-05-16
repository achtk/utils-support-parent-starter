package com.chua.common.support.monitor.session;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 代理会话
 *
 * @author CH
 */
@Getter
public class ObjectSession implements Session {
    private final Object obj;

    public ObjectSession(Object obj) {
        this.obj = obj;
    }

    @Override
    public List<Serializable[]> getBeforeData() {
        return Collections.emptyList();
    }

    @Override
    public List<Serializable[]> getModifyData() {
        return Collections.emptyList();
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
        return this;
    }
}
