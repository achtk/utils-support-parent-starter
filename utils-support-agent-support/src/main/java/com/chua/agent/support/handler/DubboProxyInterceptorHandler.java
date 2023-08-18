package com.chua.agent.support.handler;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.json.JSON;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.StringUtils;

import java.util.*;

import static com.chua.agent.support.json.serializer.SerializerFeature.PrettyFormat;


/**
 * DubboProxyInterceptorHandler
 * @author CH
 */
public class DubboProxyInterceptorHandler  implements Constant {
    /**
     * 切入点
     * @param span 切入点
     * @param objects
     */
    public static void insertPoint(Span span, Object[] objects) {
        if(null == span || StringUtils.isNullOrEmpty(span.getLinkId())) {
            return;
        }

        Object getContext = null;
        try {
            getContext = ClassUtils.invoke("getContext", Thread.currentThread().getContextClassLoader().loadClass("org.apache.dubbo.rpc.RpcContext"), null, new Object[0]);
            ClassUtils.invoke("setAttachment", getContext, LINK_ID, span.getLinkId());
            ClassUtils.invoke("setAttachment", getContext, LINK_PID, span.getPid());
        } catch (Exception ignored) {
        }
    }

    public static boolean hasLinkId() {
       return !StringUtils.isNullOrEmpty(getLinkId());
    }

    public static String getLinkId() {
        Object getContext = null;
        try {
            getContext = ClassUtils.invoke("getContext", Thread.currentThread().getContextClassLoader().loadClass("org.apache.dubbo.rpc.RpcContext"), null, new Object[0]);
            return (String) ClassUtils.invoke("getAttachment", getContext, LINK_ID);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getLinkParentId() {
        Object getContext = null;
        try {
            getContext = ClassUtils.invoke("getContext", Thread.currentThread().getContextClassLoader().loadClass("org.apache.dubbo.rpc.RpcContext"), null, new Object[0]);
            return (String) ClassUtils.invoke("getAttachment", getContext, LINK_PID);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean hasLinkParentId() {
        return !StringUtils.isNullOrEmpty(getLinkParentId());
    }

    public static void insertResponsePoint(Object call) {
        Stack<Span> spans = NewTrackManager.currentSpans();
        List<Span> spans1 = new LinkedList<>(spans);
        if(spans1.size() < 1) {
            return;
        }


        List<Span> spans2  = new LinkedList<>();
        Span pSpan = null;
        Map<String, String> cache = new LinkedHashMap<>();
        for (Span span : spans1) {
            span.setStack(Collections.emptyList());
            if(!StringUtils.isNullOrEmpty(span.getTypeMethod()) ) {
                String pid = null;
                if(null != pSpan) {
                    if(pSpan.getTypeMethod().equals(span.getTypeMethod())) {
                        continue;
                    }
                    if(cache.containsKey(span.getId())) {
                        pid = cache.get(span.getId());
                    } else {
                        pid = pSpan.getId();
                        cache.put(span.getId(), pSpan.getId());
                    }
                    span.setPid(pid);
                }
                spans2.add(span);
                pSpan = span;
            }
        }

        if(null != call && "org.apache.dubbo.rpc.AsyncRpcResult".equalsIgnoreCase(call.getClass().getTypeName())) {
            ClassUtils.invoke("setAttachment", call, LINK_RES_SPAN, StringUtils.gzip(JSON.toJSONBytes(spans2)));
        }
    }

    public static void receivePoint(Object invoke) {
        if(null != invoke && "org.apache.dubbo.rpc.AsyncRpcResult".equalsIgnoreCase(invoke.getClass().getTypeName())) {
            String attachment = (String) ClassUtils.invoke("getAttachment", invoke, LINK_RES_SPAN);
            if(null == attachment) {
                return;
            }
            Span lastSpan = NewTrackManager.getLastSpan();
            if(null == lastSpan) {
                return;
            }

            Object getValue = ClassUtils.invoke("getValue", invoke);
            registerSpan(getValue, attachment, lastSpan);
        }
    }



    private static void registerSpan(Object value, String trim, Span lastSpan) {
        String pid = lastSpan.getId();
        List<Span> spans = StringUtils.unGzip(trim);
        for (int i = 0, spansSize = spans.size(); i < spansSize; i++) {
            Span span = spans.get(i);
            if(i == 0) {
                span.setPid(pid);
            }

            if(i == spansSize - 1) {
                span.setHeader(Collections.singletonList(JSON.toJSONString(value, PrettyFormat)));
            }

            if(StringUtils.isNullOrEmpty(span.getTypeMethod())) {
                span.setTypeMethod(span.getMessage());
            }
            NewTrackManager.registerSpan(span);
        }
    }
}
