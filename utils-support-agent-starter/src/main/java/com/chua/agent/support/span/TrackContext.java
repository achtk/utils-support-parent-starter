package com.chua.agent.support.span;

/**
 * @author CH
 */
public class TrackContext {

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    public static String getLinkId() {
        return THREAD_LOCAL.get();
    }

    public static void setLinkId(String linkId) {
        THREAD_LOCAL.set(linkId);
    }

}