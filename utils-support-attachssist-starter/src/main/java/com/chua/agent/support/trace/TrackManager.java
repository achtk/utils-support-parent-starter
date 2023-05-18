//package com.chua.agent.support.trace;
//
//import com.chua.agent.support.span.span.Span;
//import com.chua.agent.support.ws.SimpleWsServer;
//
//import java.lang.reflect.Method;
//import java.util.Stack;
//import java.util.UUID;
//import java.util.concurrent.Callable;
//
///**
// * 追踪管控
// */
//public class TrackManager {
//
//    private static final ThreadLocal<Stack<Span>> TRACK = new ThreadLocal<>();
//    private static final ThreadLocal<Stack<Span>> TRACK_CACHE = new ThreadLocal<>();
//
//
//    private static Span createSpan() {
//        Stack<Span> stack = TRACK.get();
//        if (stack == null) {
//            stack = new Stack<>();
//            TRACK.remove();
//            TRACK.set(stack);
//        }
//        String linkId;
//        if (stack.isEmpty()) {
//            linkId = TrackContext.getLinkId();
//            if (linkId == null) {
//                linkId = "nvl";
//                TrackContext.setLinkId(linkId);
//            }
//        } else {
//            Span span = stack.peek();
//            linkId = span.getLinkId();
//            TrackContext.setLinkId(linkId);
//        }
//        return new Span(linkId);
//    }
//
//    public static Span createEntrySpan() {
//        Span span = createSpan();
//        Stack<Span> stack = TRACK.get();
//        if (stack.isEmpty()) {
//            Stack<Span> spans = TRACK_CACHE.get();
//            if (null != spans) {
//                TRACK_CACHE.get().clear();
//                TRACK_CACHE.remove();
//            }
//            span.setId(span.getLinkId());
//        } else {
//            span.setId(UUID.randomUUID().toString());
//            span.setPid(stack.peek().getId());
//        }
//        stack.push(span);
//        return span;
//    }
//
//
//    public static Span getExitSpan() {
//        Stack<Span> stack = TRACK.get();
//        if (stack == null || stack.isEmpty()) {
//            TrackContext.clear();
//            return null;
//        }
//        return stack.pop();
//    }
//
//    public static void registerSpan(Span span) {
//        Stack<Span> spans = TRACK_CACHE.get();
//        if (spans == null) {
//            spans = new Stack<>();
//        }
//        spans.push(span);
//        TRACK_CACHE.remove();
//        TRACK_CACHE.set(spans);
//    }
//
//    public static Span getCurrentSpan() {
//        Stack<Span> stack = TRACK.get();
//        if (stack == null || stack.isEmpty()) {
//            return null;
//        }
//        return stack.peek();
//    }
//
//    /**
//     * 注册
//     *
//     * @param exitSpan span
//     */
//    public static void register(Span exitSpan) {
//    }
//
//    public static void before() {
//        Span currentSpan = TrackManager.getCurrentSpan();
//        if (null == currentSpan) {
//            String linkId = UUID.randomUUID().toString();
//            TrackContext.setLinkId(linkId);
//        }
//        TrackManager.createEntrySpan();
//    }
//
//
//    public static void after(Method method) {
//        Span exitSpan = TrackManager.getExitSpan();
//        if (null == exitSpan) {
//            return;
//        }
//        String className = method.getDeclaringClass().getName();
//        String methodName = method.getName();
//        exitSpan.setCostTime((System.currentTimeMillis() - exitSpan.getEnterTime().getTime()));
//        exitSpan.setMessage("链路追踪(MQ)：" + exitSpan.getLinkId() + " " + className + "." + methodName + " 耗时：" + exitSpan.getCostTime() + "ms");
//        exitSpan.setStack(Thread.currentThread().getStackTrace());
//        exitSpan.setTypeMethod(className + "." + methodName);
//        exitSpan.setMethod(method.getDeclaringClass().getSimpleName() + "." + methodName);
//        exitSpan.setType(className);
//
//        SimpleWsServer.send(exitSpan, "trace");
//
//    }
//
//    public static Object invoke(Callable<?> callable) throws Exception {
//        return callable.call();
//    }
//
//    public static Object span(Method method, Callable<?> callable) throws Exception {
//        before();
//        try {
//            return invoke(callable);
//        } finally {
//            after(method);
//        }
//    }
//
//    /**
//     * 获取栈
//     *
//     * @return 栈
//     */
//    public static Stack<Span> currentSpans() {
//        return TRACK_CACHE.get();
//    }
//
//    public static void clear() {
//        TRACK_CACHE.remove();
//        TRACK.remove();
//    }
//}