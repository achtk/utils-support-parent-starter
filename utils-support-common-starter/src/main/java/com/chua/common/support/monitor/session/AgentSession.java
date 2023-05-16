package com.chua.common.support.monitor.session;

import com.chua.common.support.utils.StringUtils;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 代理会话
 *
 * @author CH
 */
@Getter
public class AgentSession implements Session {
    private final Object obj;
    private final Method method;
    private final Object[] args;

    public AgentSession(Object obj, Method method, Object[] args) {
        this.obj = obj;
        this.method = method;
        this.args = args;
    }

    @Override
    public List<Serializable[]> getBeforeData() {
        return Collections.emptyList();
    }

    @Override
    public List<Serializable[]> getModifyData() {
        List<Serializable[]> rs = new LinkedList<>();
        for (Object arg : args) {
            rs.add(new String[]{StringUtils.utf8Str(arg)});
        }
        return rs;
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
