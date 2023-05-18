package com.chua.agent.support.pointer;


import com.chua.agent.support.annotation.Spi;
import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringJoiner;

/**
 * zookeeper
 *
 * @author CH
 * @since 2022-02-09
 */
@Spi("zookeeper")
public class ZookeeperServerPoint implements ServerPoint {
    @Override
    public List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span) {
        Class<?> aClass = obj.getClass();
        try {
            Field hostProvider = aClass.getDeclaredField("hostProvider");
            hostProvider.setAccessible(true);
            Object sendThread = hostProvider.get(obj);
            Class<?> aClass1 = sendThread.getClass();
            Field serverAddresses = aClass1.getDeclaredField("serverAddresses");
            serverAddresses.setAccessible(true);
            List o = (List) serverAddresses.get(sendThread);
            StringJoiner stringJoiner = new StringJoiner(",");
            for (Object o1 : o) {
                stringJoiner.add(o1.toString());
            }
            span.setMessage(stringJoiner.toString());
            span.setType("zookeeper(" + span.getMessage() + ")");
            span.setId(span.getType());
            span.setEx("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] filterType() {
        return new String[]{"org.apache.zookeeper.ClientCnxn"};
    }

    @Override
    public String[] filterMethod() {
        return new String[]{"start"};
    }
}
