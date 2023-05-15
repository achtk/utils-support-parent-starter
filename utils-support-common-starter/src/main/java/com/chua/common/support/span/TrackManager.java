package com.chua.common.support.span;
import com.chua.common.support.lang.StopWatch;

import java.util.Stack;
import java.util.UUID;

/**
 * 追踪管控
 *
 * @author CH
 */
public class TrackManager {

    private static final ThreadLocal<Stack<Span>> TRACK = new ThreadLocal<>();
    private static final ThreadLocal<Stack<Span>> TRACK_CACHE = new ThreadLocal<>();

    /**
     * 注册span
     *
     * @param span span
     */
    public static void registerSpan(Span span) {
        Stack<Span> spans = TRACK_CACHE.get();
        if (spans == null) {
            spans = new Stack<>();
        }
        spans.push(span);
        TRACK_CACHE.set(spans);
    }

    /**
     * 创建span
     *
     * @param name 名称
     * @return 名称
     */
    private static Span createSpan(String name) {
        Stack<Span> stack = TRACK.get();
        StopWatch stopWatch = null;
        if (stack == null) {
            stack = new Stack<>();
            stopWatch = new StopWatch(name);
            TRACK.remove();
            TRACK.set(stack);
        }
        String linkId;
        if (stack.isEmpty()) {
            linkId = TrackContext.getLinkId();
            if (linkId == null) {
                linkId = "nvl";
                TrackContext.setLinkId(linkId);
            }
        } else {
            Span span = stack.peek();
            linkId = span.getLinkId();
            TrackContext.setLinkId(linkId);
        }
        return new Span(linkId, name, stopWatch);
    }

    /**
     * 创建span
     *
     * @param name 名称
     * @return 名称
     */
    public static Span createEntrySpan(String name) {
        Span span = createSpan(name);
        Stack<Span> stack = TRACK.get();
        if (stack.isEmpty()) {
            Stack<Span> spans = TRACK_CACHE.get();
            if (null != spans) {
                TRACK_CACHE.remove();
                spans.clear();
            }
            span.setId(span.getLinkId());
        } else {
            span.setId(UUID.randomUUID().toString());
            span.setPid(stack.peek().getId());
        }
        stack.push(span);
        return span;
    }

    /**
     * 获取当前span
     *
     * @return span
     */
    public static Span getCurrentSpan() {
        Stack<Span> stack = TRACK.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    /**
     * 获取第一个span
     *
     * @return span
     */
    public static Span getExitSpan() {
        Stack<Span> stack = TRACK.get();
        if (stack == null || stack.isEmpty()) {
            TrackContext.clear();
            return null;
        }
        return stack.pop();
    }

    /**
     * 获取栈
     *
     * @return 栈
     */
    public static Stack<Span> currentSpans() {
        return TRACK_CACHE.get();
    }
}