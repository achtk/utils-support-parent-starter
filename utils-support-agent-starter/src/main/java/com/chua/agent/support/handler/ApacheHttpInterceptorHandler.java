package com.chua.agent.support.handler;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.agent.support.store.AgentStore.log;

/**
 * 处理器
 *
 * @author CH
 */
public class ApacheHttpInterceptorHandler implements Constant {

   static Pattern compile = Pattern.compile("\\[(.*?)\\]");

    /**
     * 解析链路
     *
     * @param call   结果
     * @param target 请求对象
     * @param method 方法
     * @param args   参数
     */
    public static void sendTrace(Object call, Object target, Method method, Object[] args) {
        String name = method.getName();
        if ("execute".equals(name)) {
            doAnalysisExecute(target, method, args);
            return;
        }

        doAnalysisResponse(call, target, method, args);
    }

    /**
     * httpclient
     * @param target 请求对象
     * @param method 方法
     * @param args  参数
     */
    private static void doAnalysisDoExecute(Object target, Method method, Object[] args) {
        Span exitSpan = NewTrackManager.getLastSpan();
        if (null == exitSpan) {
            return;
        }

        Object arg =  args[1];
        doAnalysisExecute(target, method, new Object[]{arg});
        doAnalysisResponse(null, target, method, new Object[]{arg});
    }

    /**
     * 分布式链路
     *
     * @param target
     * @param method
     * @param args   参数
     */
    private static void doAnalysisExecute(Object target, Method method, Object[] args) {
        Object httpRequest =  args[0];
        Span exitSpan = NewTrackManager.getLastSpan();
        if (null == exitSpan) {
            return;
        }
        String linkId = exitSpan.getLinkId();
        String pid = exitSpan.getPid();
        ClassUtils.invoke("addHeader", httpRequest, LINK_ID, linkId);
        ClassUtils.invoke("addHeader", httpRequest, LINK_PID, pid);
        doAnalysisHttpResponse(null, target,  method, args[0]);
    }
    /**
     * 解析链路
     *
     * @param call   结果
     * @param target 请求对象
     * @param method 方法
     * @param args   参数
     */
    private static void doAnalysisResponse(Object call, Object target, Method method, Object[] args) {
        Object arg = args[0];
        if(null != arg && "org.apache.http.client.methods.HttpRequestWrapper$HttpEntityEnclosingRequestWrapper".equalsIgnoreCase(arg.getClass().getTypeName())) {
            doAnalysisHttpResponse(call, target, method, arg);
            return;
        }

        Span lastSpan = NewTrackManager.getLastSpan();
        if (null == lastSpan) {
            return;
        }
        Object arg1 = args[1];

        Span sql1 = NewTrackManager.createEntrySpan();
        sql1.setEnterTime(new Date());
        sql1.setId(UUID.randomUUID().toString());
        sql1.setMessage(arg1.toString());
        sql1.setMethod(method.getName());
        sql1.setTypeMethod(target.getClass().getTypeName() + "." + sql1.getMethod());
        sql1.setType(target.getClass().getTypeName());
    }

    private static void doAnalysisHttpResponse(Object call, Object target, Method method, Object arg) {
        Span lastSpan = NewTrackManager.getLastSpan();
        if (null == lastSpan) {
            return;
        }

        Object requestLine = ClassUtils.invoke("getRequestLine", arg);

        Object methodName = ClassUtils.invoke("getMethod", requestLine);
        Object uri = ClassUtils.invoke("getUri", requestLine);
        Object target1 = ClassUtils.getObject("target", arg);
        log(Level.INFO, lastSpan.getMessage());

        List<String> stack = new LinkedList<>();
        stack.add("" + (null == target1 ? "" : target1.toString()) + uri);
        stack.add("<strong class='node-details__name collapse-handle'>请求头</strong>");
        Object[] allHeaders = (Object[]) ClassUtils.invoke("getAllHeaders", arg);
        for (Object allHeader : allHeaders) {
            String s = allHeader.toString();
            if(s.startsWith("x-request")) {
                continue;
            }
            stack.add(s);
        }

        stack.add("<strong class='node-details__name collapse-handle'>请求体</strong>");
        try {
            stack.add(new String((byte[]) ClassUtils.getObject(0, ClassUtils.getObject("entity", arg))));
        } catch (Exception ignored) {
        }
        stack.add("<strong class='node-details__name collapse-handle'>响应头</strong>");

        Span sql1 = NewTrackManager.createEntrySpan();
        sql1.setEnterTime(new Date());
        sql1.setId(UUID.randomUUID().toString());
        sql1.setMessage(methodName + " " + target1 + uri);
        sql1.setMethod(method.getName());
        sql1.setTypeMethod(target.getClass().getTypeName() + "." + sql1.getMethod());
        sql1.setType(target.getClass().getTypeName());

        if (null != call) {
            Span sql2 = NewTrackManager.createEntrySpan();
            sql2.setEnterTime(new Date());
            sql2.setId(UUID.randomUUID().toString());
            sql2.setTypeMethod("<span class='badge badge-primary'>" + methodName + " " + (null == target1 ? "" : target1) + uri + "</span>");
            sql2.setHeader(stack);
            sql2.setMethod(method.getName());
            sql2.setType(target.getClass().getTypeName());


            String s = call.toString();
            Matcher matcher = compile.matcher(s);
            if(matcher.find()) {
                String group = matcher.group();
                Map<String, String> tpl = new LinkedHashMap<>();
                for (String s1 : group.split(",")) {
                    String replace = s1.trim().replace("[", "").replace("]", "");
                    String[] split = replace.split(":");
                    try {
                        tpl.put(split[0], split[1]);
                    } catch (Exception ignored) {
                    }
                }
                if(tpl.containsKey(LINK_RES_SPAN)) {
                    registerSpan(target1, tpl.get(LINK_RES_SPAN), sql2.getId());
                }
            }
        }

    }

    private static void registerSpan(Object target1, String trim, String pid) {
        List<Span> spans = StringUtils.unGzip(trim);
        for (int i = 0, spansSize = spans.size(); i < spansSize; i++) {
            Span span = spans.get(i);
            if(i == 0) {
                span.setPid(pid);
            }


            if("sql".equals(span.getModel())) {
                span.setHeader(Collections.singletonList(span.getMessage()));
            }

            span.setTypeMethod("<span class='badge badge-primary'>" + target1 +"</span>" + span.getTypeMethod());
            NewTrackManager.registerSpan(span);
        }
    }
}
