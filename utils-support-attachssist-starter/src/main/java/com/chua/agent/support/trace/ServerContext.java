package com.chua.agent.support.trace;

/**
 * 博客：http://itstack.org
 * 论坛：http://bugstack.cn
 * 公众号：bugstack虫洞栈  ｛获取学习源码｝
 * create by fuzhengwei on 2019
 */
public class ServerContext {

    private static final ThreadLocal<String> TRACK_LOCAL = new ThreadLocal<>();

    public static void clear() {
        TRACK_LOCAL.remove();
    }

    public static String getLinkId() {
        return TRACK_LOCAL.get();
    }

    public static void setLinkId(String linkId) {
        TRACK_LOCAL.set(linkId);
    }

}