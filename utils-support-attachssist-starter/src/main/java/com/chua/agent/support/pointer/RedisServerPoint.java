package com.chua.agent.support.pointer;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Spi;
import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * redis
 *
 * @author CH
 */
@Spi("redis")
public class RedisServerPoint implements ServerPoint {
    @Override
    public List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span) {

        if (objects.length != 1) {
            return null;
        }

        //jedis
        if (obj.getClass().getName().endsWith("Jedis")) {
            return analysisJedis(objects, method, obj, span);
        }

        return null;
    }

    private List<Span> analysisJedis(Object[] objects, Method method, Object obj, Span span) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(objects[0]));

        Class<?> declaringClass = method.getDeclaringClass();
        Field socket = null;
        try {
            socket = declaringClass.getDeclaredField("client");
        } catch (Exception ignored) {
        }
        if (null == socket) {
            return null;
        }
        socket.setAccessible(true);
        Object o = null;
        try {
            o = socket.get(obj);
        } catch (IllegalAccessException ignored) {
        }

        if (null != o) {
            try {
                socket = o.getClass().getSuperclass().getSuperclass().getDeclaredField("socketFactory");
            } catch (NoSuchFieldException ignored) {
                socket = null;
            }
        }

        if (null == socket) {
            return null;
        }

        socket.setAccessible(true);
        try {
            o = socket.get(o);
        } catch (IllegalAccessException e) {
            return null;
        }

        Class<?> aClass = o.getClass();
        int port = 0;
        String host = null;
        try {
            Method getHost = aClass.getDeclaredMethod("getHost");
            Method getPort = aClass.getDeclaredMethod("getPort");
            getHost.setAccessible(true);
            getPort.setAccessible(true);

            port = (int) getPort.invoke(o);
            host = (String) getHost.invoke(o);
            span.setMessage(host + ":" + port);
            span.setType("redis(" + span.getMessage() + ")");
            span.setId(span.getType());
            span.setEx("localhost");
            span.setDb(jsonObject.getString("database"));
            span.setMethod(jsonObject.getString("user"));
            span.setTypeMethod(jsonObject.getString("password"));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }

    @Override
    public String[] filterType() {
        return new String[]{"redis.clients.jedis.BinaryJedis"};
    }

    @Override
    public String[] filterMethod() {
        return new String[]{"initializeFromClientConfig"};
    }
}
