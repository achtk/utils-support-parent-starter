package com.chua.agent.support.pointer;


import com.chua.agent.support.annotation.Spi;
import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Monogo
 *
 * @author CH
 * @since 2022-02-09
 */
@Spi("mongo")
public class MonogoServerPoint implements ServerPoint {
    @Override
    public List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span) {
        Class<?> aClass = obj.getClass();
        Object o = null;
        try {
            Field address = aClass.getDeclaredField("address");
            address.setAccessible(true);
            o = address.get(obj);
        } catch (Exception ignored) {
        }
        if (null != o) {
            try {
                span.setType("mongodb(" + span.getMessage() + ")");
                span.setMessage(o.toString());
                span.setId(span.getType());
                span.setEx("localhost");
            } catch (Exception ignored) {
            }

        }

        return null;
    }

    @Override
    public String[] filterType() {
        return new String[]{"com.mongodb.internal.connection.SocketStream"};
    }

    @Override
    public String[] filterMethod() {
        return new String[]{"connect", "bind", "initializeSocket"};
    }
}
