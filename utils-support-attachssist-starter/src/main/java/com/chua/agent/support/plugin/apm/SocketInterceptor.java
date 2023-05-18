//package com.chua.agent.support.plugin.apm;
//
//import com.chua.agent.support.annotation.Spi;
//import com.chua.agent.support.pointer.ServerPoint;
//import com.chua.agent.support.span.span.Span;
//import com.chua.agent.support.trace.ServerContext;
//import com.chua.agent.support.trace.ServerManager;
//import net.bytebuddy.description.NamedElement;
//import net.bytebuddy.description.type.TypeDescription;
//import net.bytebuddy.dynamic.DynamicType;
//import net.bytebuddy.implementation.MethodDelegation;
//import net.bytebuddy.implementation.bind.annotation.*;
//import net.bytebuddy.matcher.ElementMatcher;
//import net.bytebuddy.matcher.ElementMatchers;
//
//import java.lang.reflect.Method;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.security.MessageDigest;
//import java.util.*;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//
///**
// * @author CH
// * @since 2021-08-27
// */
//public class SocketInterceptor implements Interceptor {
//
//    private static final Map<String, String> CONNECT = new ConcurrentHashMap<>();
//    private static final Map<String, String> RELATION = new ConcurrentHashMap<>();
//    private static final List<Span> RELATIONS = new CopyOnWriteArrayList<>();
//    private static final List<String> RELATIONS_IDS = new CopyOnWriteArrayList<>();
//    private static final String LOCAL = "127.0.0.1";
//    private static final ThreadLocal<Stack<Span>> TRACK = new ThreadLocal<>();
//    private static final Map<String, ServerPoint> SERVER_POINT_MAP = new ConcurrentHashMap<>();
//
//
//    static {
//        CONNECT.put(LOCAL, LOCAL);
//        ServiceLoader<ServerPoint> load = ServiceLoader.load(ServerPoint.class);
//        for (ServerPoint serverPoint : load) {
//            Class<? extends ServerPoint> aClass = serverPoint.getClass();
//            Spi spi = aClass.getDeclaredAnnotation(Spi.class);
//            if (null == spi) {
//                continue;
//            }
//
//            SERVER_POINT_MAP.put(spi.value(), serverPoint);
//        }
//
//    }
//
//    public Map<String, String> getConnect() {
//        return CONNECT;
//    }
//
//    public List<Span> getRelations() {
//        return RELATIONS;
//    }
//
//    public Map<String, String> getRelation() {
//        return RELATION;
//    }
//
//    @Override
//    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder) {
//        List<String> collect = SERVER_POINT_MAP.values().stream().map(ServerPoint::filterMethod)
//                .map(Arrays::asList)
//                .flatMap(Collection::stream)
//                .distinct()
//                .collect(Collectors.toList());
//
//        String s = collect.get(0);
//        ElementMatcher.Junction<NamedElement> named = ElementMatchers.named(s);
//        for (int i = 1; i < collect.size(); i++) {
//            String item = collect.get(i);
//            named = named.or(ElementMatchers.named(item));
//        }
//        return builder.method(named).intercept(MethodDelegation.to(SocketInterceptor.class));
//    }
//
//    @Override
//    public ElementMatcher<? super TypeDescription> type() {
//        List<String> collect = SERVER_POINT_MAP.values().stream().map(ServerPoint::filterType)
//                .map(Arrays::asList)
//                .flatMap(Collection::stream)
//                .distinct()
//                .collect(Collectors.toList());
//
//        ElementMatcher.Junction<TypeDescription> or = ElementMatchers.hasSuperType(ElementMatchers.named(Socket.class.getName()))
//                .or(ElementMatchers.named(Socket.class.getName()))
//                .or(ElementMatchers.named(ServerSocket.class.getName()));
//        for (int i = 0; i < collect.size(); i++) {
//            String item = collect.get(i);
//            or = or.or(ElementMatchers.named(item))
//                    .or(ElementMatchers.hasSuperType(ElementMatchers.named(item)));
//        }
//        return or;
//    }
//
//    @RuntimeType
//    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
//        Span currentSpan = ServerManager.getCurrentSpan();
//        if (null == currentSpan) {
//            String linkId = UUID.randomUUID().toString();
//            ServerContext.setLinkId(linkId);
//        }
//        ServerManager.createEntrySpan();
//        try {
//            return callable.call();
//        } finally {
//            Span exitSpan = ServerManager.getExitSpan();
//            if (null != exitSpan) {
//                exitSpan.setType("server");
//                List<Span> list = doAnalaysisServer(objects, method, obj, exitSpan);
//                ServerManager.registerSpan(exitSpan);
//                if (null == currentSpan) {
//                    Stack<Span> spans = ServerManager.currentSpans();
//                    List<Span> spans1 = new LinkedList<>(spans);
//                    if (null != list) {
//                        spans1.addAll(list);
//                    }
//                    //Collections.reverse(spans1);
//                    for (Span span : spans1) {
//                        if ("".equals(span.getEx())) {
//                            continue;
//                        }
//
//                        try {
//                            analysis(span.getId(), span.getEx());
//                        } catch (Throwable ignored) {
//                        }
//
//                        if (null != span.getTypeMethod()) {
//                            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//                            messageDigest.update(span.getTypeMethod().getBytes(UTF_8));
//                            span.setTypeMethod(new String(messageDigest.digest(), UTF_8));
//                        }
//                        RELATIONS.add(span);
//                    }
//                }
//
//            }
//        }
//    }
//
//    private static void analysis(String id, String ex) {
//        if (!RELATIONS_IDS.contains(id)) {
//            RELATIONS_IDS.add(id);
//        } else {
//            Iterator<Span> iterator = RELATIONS.iterator();
//            Span del = null;
//            while (iterator.hasNext()) {
//                Span next = iterator.next();
//                if (null == next.getEx()) {
//                    continue;
//                }
//
//                if (id.equals(next.getId()) && next.getEx().equals(ex)) {
//                    del = next;
//                    break;
//                }
//            }
//            if (null != del) {
//                RELATIONS.remove(del);
//            }
//        }
//    }
//
//    /**
//     * 分析地址
//     *
//     * @param objects 参数
//     * @param method  方法
//     * @param obj     对象
//     */
//    private static List<Span> doAnalaysisServer(Object[] objects, Method method, Object obj, Span span) {
//        String name = method.getDeclaringClass().getName();
//        for (Map.Entry<String, ServerPoint> entry : SERVER_POINT_MAP.entrySet()) {
//            String key = entry.getKey();
//            if (name.contains(key)) {
//                return entry.getValue().doAnalysis(objects, method, obj, span);
//            }
//        }
//        return null;
//    }
//
//    static Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
//
//    public static void registerNode(String server, String type, String to) {
//        Matcher m = p.matcher(server);
//
//        String id = type + "(" + server + ")";
//
//        if(m.find()) {
//            id = m.group(0);
//        }
//        analysis(id, to);
//        Span span = new Span();
//        span.setEx(to);
//        span.setMessage(id);
//        span.setId(server);
//        span.setType(type + "(" + id + ")");
//        RELATIONS.add(span);
//    }
//
//}
