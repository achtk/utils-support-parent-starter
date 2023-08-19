package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.handler.DubboProxyInterceptorHandler;
import com.chua.agent.support.handler.SpringInterceptorHandler;
import com.chua.agent.support.json.JSON;
import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import com.chua.agent.support.span.TrackContext;
import com.chua.agent.support.store.TransPointStore;
import com.chua.agent.support.thread.DefaultThreadFactory;
import com.chua.agent.support.transfor.CloseHttpClientTransfer;
import com.chua.agent.support.transfor.HttpClientTransfer;
import com.chua.agent.support.transfor.TomcatTransfer;
import com.chua.agent.support.transfor.Transfer;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.StringUtils;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * servlet
 *
 * @author CH
 * @since 2021-08-25
 */
public class SpringInterceptor implements Interceptor {

    private static final int PRE_INDEX = Runtime.getRuntime().availableProcessors() * 2;
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(PRE_INDEX, new DefaultThreadFactory());
    private static final Map<String, Transfer> MAPPING = new HashMap<>();
    public static Class<?> DUBBO;

    static {
        try {
            DUBBO = Class.forName("org.apache.dubbo.rpc.RpcContext");
        } catch (ClassNotFoundException ignored) {
        }
        List<Class<?>> classes = new LinkedList<>();
        classes.add(CloseHttpClientTransfer.class);
        classes.add(HttpClientTransfer.class);
        classes.add(TomcatTransfer.class);

        for (Class<?> aClass : classes) {
            if (Transfer.class.isAssignableFrom(aClass)) {
                Transfer instance = null;
                try {
                    instance = (Transfer) aClass.newInstance();
                } catch (Exception ignored) {
                }
                if (null == instance) {
                    continue;
                }

                MAPPING.put(instance.name(), instance);
            }
        }
    }

    @RuntimeType
    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        if (Modifier.isNative(method.getModifiers())) {
            return callable.call();
        }


        Span currentSpan = NewTrackManager.getCurrentSpan();
        if (null == currentSpan) {
            String linkId = getRequestLinkId(objects);

            if (null == linkId) {
                linkId = UUID.randomUUID().toString();
            }

            TrackContext.setLinkId(linkId);
        }

        Span entrySpan = NewTrackManager.createEntrySpan();
        String pId = getRequestLinkParentId(objects);
        if (!StringUtils.isNullOrEmpty(pId)) {
            entrySpan.setPid(pId);
        }
        try {
            return callable.call();
        } finally {
            endSpan(currentSpan, entrySpan, method, obj, objects);
        }
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasAnnotation(
                ElementMatchers.annotationType(
                        ElementMatchers.named("org.springframework.web.bind.annotation.RestController")
                                .or(ElementMatchers.named("org.springframework.stereotype.Controller"))
                                .or(ElementMatchers.named("org.springframework.stereotype.Service"))
                                .or(ElementMatchers.named("org.aspectj.lang.annotation.Aspect"))
                                .or(ElementMatchers.named("org.apache.ibatis.annotations.Mapper"))
                                .or(ElementMatchers.named("org.springframework.stereotype.Repository"))
                )).and(ElementMatchers.not(ElementMatchers.nameStartsWith("org.springframework")));
    }

    protected static String getRequestLinkParentId(Object[] args) {
        if (SpringInterceptorHandler.hasLinkParentId(args)) {
            return SpringInterceptorHandler.getLinkParentId(args);
        }

        if (null != DUBBO && DubboProxyInterceptorHandler.hasLinkParentId()) {
            return DubboProxyInterceptorHandler.getLinkParentId();
        }

        return null;
    }

    /**
     * http response
     *
     * @param args 参数
     * @return response
     */
    private static Object getResponse(Object[] args) {
        return SpringInterceptorHandler.getResponse(args);
    }

    /**
     * linkid
     *
     * @param args 参数
     * @return linkid
     */
    protected static String getRequestLinkId(Object[] args) {
        if (SpringInterceptorHandler.hasLinkId(args)) {
            return SpringInterceptorHandler.getLinkId(args);
        }

        if (null != DUBBO && DubboProxyInterceptorHandler.hasLinkId()) {
            return DubboProxyInterceptorHandler.getLinkId();
        }

        return null;
    }

    private static void endSpan(Span currentSpan, Span lastSpan, Method method, Object obj, Object[] objects) {
        NewTrackManager.refreshCost(lastSpan);
        NewTrackManager.refreshCost(currentSpan);
        if (null != lastSpan) {
            Interceptor.doRefreshSpan(method, objects, lastSpan);

            Class<?> aClass = obj.getClass();
            String name = aClass.getName();
            if (null == currentSpan) {
                Stack<Span> spans = NewTrackManager.currentSpans();
                NewTrackManager.clear();
                List<Span> spans1 = new LinkedList<>(spans);

                rebase(name, objects, spans1);
                refix(spans1);
                if (spans1.size() == 1) {
                    if ("java.lang.Object".equals(spans1.get(0).getType())) {
                        //
                    } else {
                    }
                } else {
                    //  Collections.reverse(spans1);
                    Set<Span> rs = new LinkedHashSet<>(spans);
                    if (spans1.isEmpty()) {
                        return;
                    }

                    List<Span> result = new LinkedList<>(rs);
                    Span span1 = result.get(0);
                    Span span2 = result.get(result.size() - 1);
                    if (null == span1.getEx()) {
                        span1.setEx(span2.getEx());
                    }

                    sendSpan(result);
                }

            } else {
                rebase(name, objects, Collections.singletonList(lastSpan));
            }
        }
    }

    public static void sendRemoteSpan(List<Span> result, Object[] objects) {
        String requestLinkId = getRequestLinkId(objects);
        if (!StringUtils.isNullOrEmpty(requestLinkId)) {
            Object response = getResponse(objects);
            for (Span span : result) {
                span.setHeader(Collections.emptyList());
                span.setStackTrace(new StackTraceElement[0]);
            }
            try {
                ClassUtils.invoke("addHeader", response, "x-response-span", StringUtils.gzip(JSON.toJSONBytes(result)));
                ClassUtils.invoke("addHeader", response, "x-response-pid", getRequestLinkParentId(objects));
            } catch (Exception ignored) {
            }
        }
    }

    private static void sendSpan(List<Span> result) {
        Span span1 = result.get(0);
        if(StringUtils.isNullOrEmpty(span1.getEx())) {
            return;
        }
        List<Span> spans2 = new LinkedList<>();
        Span pSpan = null;
        Map<String, String> cache = new LinkedHashMap<>();
        for (Span span : result) {
            if (!StringUtils.isNullOrEmpty(span.getTypeMethod())) {
                String pid = null;
                if (null != pSpan) {
                    if (pSpan.getTypeMethod().equals(span.getTypeMethod())) {
                        continue;
                    }
                    if (cache.containsKey(span.getId())) {
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

        spans2 = repackage(spans2);
        CustomTreeNode customTreeNode = new CustomTreeNode();
        customTreeNode.add(spans2);
        TransPointStore.INSTANCE.publish("trace", JSON.toJSONString(customTreeNode.transferAll()));
    }

    private static List<Span> repackage(List<Span> spans2) {
        List<Span> rs = new LinkedList<>();
        for (Span span : spans2) {
            if (span.getType().equals("java.lang.Object.toString")) {
                continue;
            }
            rs.add(span);
            if (span.getParents().isEmpty()) {
                continue;
            }

            if (null == span.getFrom()) {
                continue;
            }

            String newPid = getNewPid(rs, span);
            if (null == newPid) {
                continue;
            }

            if (span.getId().equals(newPid)) {
                continue;
            }
            span.setPid(newPid);
        }

        return rs;
    }

    private static String getNewPid(List<Span> source, Span span) {
        Set<String> parents = span.getParents();
        for (String parent : parents) {
            if (parent.equals(span.getFrom())) {
                continue;
            }

            String parent1 = getParent(source, parent);
            if (null != parent1) {
                if (parent1.equals(span.getId()) || parent1.equals(span.getPid())) {
                    return null;
                }
                return parent1;
            }
        }

        return null;
    }

    private static String getParent(List<Span> source, String parent) {
        String pid  = null;
        for (int i = 0; i < source.size(); i++) {
            Span span = source.get(i);
            if(null != span.getFrom() && span.getFrom().equals(parent)) {
                pid = span.getId();
            }

        }
        return pid;
    }

    private static void refix(List<Span> spans1) {
        List<Span> del = new LinkedList<>();
        List<String> sign = new LinkedList<>();
        for (Span span : spans1) {
            if ("java.lang.Object".equals(span.getType())) {
                del.add(span);
                continue;
            }

            String key = null;
            try {
                key = createKey(span);
            } catch (Exception e) {
                continue;
            }
            if (sign.contains(key)) {
                del.add(span);
                continue;
            }


            sign.add(key);
        }

        spans1.removeAll(del);

    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.named("equals")))).intercept(MethodDelegation.to(SpringInterceptor.class));
    }

    private static String createKey(Span span) {
        return span.getMessage() + span.getEnterTime() + span.getPid();
    }

    private static void rebase(String name, Object[] objects, List<Span> spans1) {
        Transfer transfer = MAPPING.get(name);
        if (null == transfer) {
            return;
        }

        transfer.transfer(objects, spans1);
    }

    private static void analysis(Set<Span> rs, List<Span> spans2, Map<String, List<Span>> rs1) {
        if (null == spans2 || spans2.isEmpty()) {
            return;
        }
        for (int i = spans2.size() - 1; i > -1; i--) {
            Span span = spans2.get(i);
            rs.add(span);
            analysis(rs, rs1.get(span.getId()), rs1);
        }
    }


}
