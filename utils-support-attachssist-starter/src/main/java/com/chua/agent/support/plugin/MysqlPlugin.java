package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.formatter.DmlFormatter;
import com.chua.agent.support.plugin.apm.Interceptor;
import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.trace.NewTrackManager;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.ws.SimpleWsServer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql
 *
 * @author CH
 */
public class MysqlPlugin implements HtmlAgentPlugin {

    public static final MysqlPlugin INSTANCE = new MysqlPlugin();

    @Path("sql")
    public String html() {
        return "sql.html";
    }

    @Override
    public String name() {
        return "mysql";
    }


    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("fillSendPacket").and(ElementMatchers.takesArgument(0, ElementMatchers.named("com.mysql.cj.QueryBindings"))))
                .intercept(MethodDelegation.to(MysqlPlugin.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named("com.mysql.cj.AbstractPreparedQuery")
                .or(ElementMatchers.named("com.mysql.jdbc.PreparedStatement"));
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

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
            analysisSql(currentDb, target, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "", sql, address);
            Span span = new Span();
            span.setEnterTime(date);
            span.setMessage(sql);
            span.setStack(Thread.currentThread().getStackTrace());
            sendTrace(currentDb, method, sql, address);

            SimpleWsServer.send(span, "sql");
        } catch (Throwable ignored) {
        }
        return callable.call();
    }

    private static final Map<Object, String> cacheAddress = new ConcurrentHashMap<>();
    private static String getAddress(Object target) {
        if(cacheAddress.size() > 1000) {
            cacheAddress.clear();
        }

        if(null == target) {
            return null;
        }

        if(cacheAddress.containsKey(target)) {
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
//            format = format
//                    .replace(" ", "<span style='margin-left:4px'></span>")
//                    .replace("\r\n", "<br />");

            List<String> stack = new LinkedList<>();
            stack.add(format);
            Span sql1 = new Span();
            sql1.setLinkId(lastSpan.getLinkId());
            sql1.setPid(lastSpan.getId());
            sql1.setEnterTime(new Date());
            sql1.setDb(address);
            sql1.setId(UUID.randomUUID().toString());
            sql1.setMessage( sql);
            sql1.setHeader(stack);
            sql1.setMethod(method.getName());
            sql1.setTypeMethod("<span class='badge badge-primary' >" + currentDb + "</span>" + sql1.getMessage());
            sql1.setType(lastSpan.getType());
            sql1.setError("jdbcmysql");
            sql1.setModel("sql");
            sql1.setFrom(sql);
            sql1.setCostTime(lastSpan.getCostTime());
            sql1.setParents(Collections.singleton(lastSpan.getTypeMethod()));
            NewTrackManager.registerSpan(sql1);

//            TrackManager.unregisterSpan(exitSpan);
        }
    }

    /**
     * 解析SQL
     *
     * @param currentDb
     * @param target    对象
     * @param time      时间
     * @param sql       SQL
     * @param address
     */
    private static void analysisSql(String currentDb, Object target, String time, String sql, String address) throws Exception {
        LogAgentPlugin.intercept(target, null, new Object[]{createSql(time, currentDb, sql, address)}, null, null);
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

    /**
     * 封装sql
     *
     * @param time     时间
     * @param database 数据库
     * @param sql      sql
     * @param address
     * @return sql
     */
    private static Object createSql(String time, String database, String sql, String address) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\u001B[31m[").append(time).append("]\u001B[0;39m ");
        stringBuffer.append("\u001B[34m[").append(Thread.currentThread().getName()).append("]\u001B[0;39m ");
        stringBuffer.append("\u001B[31m[数据库: [").append(address).append("] ").append(database).append("]\u001B[0;39m ");
        stringBuffer.append("\u001B[1;35m").append(sql).append("\u001B[0;39m");
        return stringBuffer.toString();
    }

    /**
     * 获取栈
     *
     * @param time 时间
     * @param date 时间
     * @return 栈
     */
    private static synchronized void analysisStack(String time, Date date) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        List<String> result = new ArrayList<>();
        for (StackTraceElement element : stackTrace) {
            result.add(element.toString());
        }

    }

    /**
     * 执行时间
     *
     * @param localDateTime 时间
     * @return 执行时间
     */
    private static String getTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
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
}
