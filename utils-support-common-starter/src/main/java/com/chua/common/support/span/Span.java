package com.chua.common.support.span;

import com.chua.common.support.lang.StopWatch;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class Span implements Serializable {
    private StopWatch stopWatch;
    /**
     * 链路ID
     */
    private String linkId;
    /**
     * 链路ID
     */
    private String id;
    /**
     * 链路ID
     */
    private String pid;
    /**
     * 方法进入时间
     */
    private long entryTimeNanos;
    /**
     * 方法退出时间
     */
    private long outTimeNanos;
    /**
     * 名称
     */
    private String title;
    /**
     * 栈
     */
    private StackTraceElement[] stack;
    /**
     * 方法
     */
    private String method;
    /**
     * 类
     */
    private String type;

    public Span(String linkId, String name, StopWatch stopWatch) {
        this.linkId = linkId;
        this.title = name;
        this.stopWatch = stopWatch;
        this.entryTimeNanos = System.nanoTime();
    }

}