package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.formatter.DmlFormatter;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mysql
 */
public class MysqlInterceptor implements Interceptor {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("fillSendPacket"))
                .intercept(MethodDelegation.to(MysqlInterceptor.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("com.mysql.cj.AbstractPreparedQuery")
                .or(ElementMatchers.named("com.mysql.jdbc.PreparedStatement"))
                .or(ElementMatchers.hasSuperType(ElementMatchers.named("com.mysql.cj.PreparedQuery")))
                ;
    }

    /**
     * 将返回值转换成具体的方法返回值类型,加了这个注解 intercept 方法才会被执行
     *
     * @param target   目标
     * @param method   方法
     * @param objects  参数
     * @param delegate 目标对象的一个代理
     * @param callable 方法的调用者对象
     * @return 结果
     * @throws Exception ex
     */
    @RuntimeType
    public static Object intercept(
            // 被拦截的目标对象 （动态生成的目标对象）
            @This Object target,
            // 正在执行的方法Method 对象（目标对象父类的Method）
            @Origin Method method,
            // 正在执行的方法的全部参数
            @AllArguments Object[] objects,
            // 目标对象的一个代理
            @Super Object delegate,
            // 方法的调用者对象 对原始方法的调用依靠它
            @SuperCall Callable<?> callable) throws Exception {
        try {
            String sql = getSql(target);

            String address = getAddress(target);
            Date date = new Date();
            String currentDb = getCurrentDb(target);
            address = address + "/" + currentDb;
            sendTrace(currentDb, method, sql, address);
        } catch (Throwable ignored) {
        }
        return callable.call();
    }

    /**
     * 获取sql
     *
     * @param cls 对象
     * @return sql
     */
    public static String getSql(Object cls) {
        try {
            Method method = cls.getClass().getMethod("asSql");
            method.setAccessible(true);
            return method.invoke(cls).toString();
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 当前数据库
     *
     * @param cls 连接
     * @return 数据库
     */
    private static String getCurrentDb(Object cls) {
        try {
            Method method = cls.getClass().getMethod("getCurrentDatabase");
            method.setAccessible(true);
            return method.invoke(cls).toString();
        } catch (Exception ignored) {
        }
        return "";
    }

    private static final Map<Object, String> cacheAddress = new ConcurrentHashMap<>();

    private static String getAddress(Object target) {
        if (cacheAddress.size() > 1000) {
            cacheAddress.clear();
        }

        if (null == target) {
            return null;
        }

        if (cacheAddress.containsKey(target)) {
            return cacheAddress.get(target);
        }

        try {
            Object session = ClassUtils.getObject("session", target);
            Object hostinfo = ClassUtils.getObject("hostInfo", session);
            Object host = ClassUtils.getObject("host", hostinfo);
            Object port = ClassUtils.getObject("port", hostinfo);

            cacheAddress.put(session, host + ":" + port);
            return host + ":" + port;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 发送到链路
     *
     * @param currentDb
     * @param method    method
     * @param sql       sql
     * @param address
     */
    private static void sendTrace(String currentDb, Method method, String sql, String address) {
        Span lastSpan = NewTrackManager.getLastSpan();
        if (null != lastSpan) {
            Interceptor.doRefreshSpan(method, new Object[0], lastSpan);

            String format = new DmlFormatter().format(sql);

            List<String> stack = new LinkedList<>();
            stack.add(format);
            Span sql1 = new Span();
            sql1.setLinkId(lastSpan.getLinkId());
            sql1.setPid(lastSpan.getId());
            sql1.setEnterTime(new Date());
            sql1.setDb(address);
            sql1.setId(UUID.randomUUID().toString());
            sql1.setMessage(sql);
            sql1.setHeader(stack);
            sql1.setMethod(method.getName());
            sql1.setTypeMethod("<span class=\"el-tag el-tag--dark\">数据库:" + currentDb + "</span><span class=\"el-tag el-tag--warning el-tag--light\">" + sql1.getMessage() + "</span>");
            sql1.setType(lastSpan.getType());
            sql1.setError("jdbcmysql");
            sql1.setModel("sql");
            sql1.setFrom(sql);
            sql1.setCostTime(lastSpan.getCostTime());
            sql1.setParents(Collections.singleton(lastSpan.getTypeMethod()));
            NewTrackManager.registerSpan(sql1);
        }
    }

}
