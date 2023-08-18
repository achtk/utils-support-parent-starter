package com.chua.example.other;

/**
 * 执行器
 * @author CH
 */
public interface Invoker {
    /**
     * 处理
     *
     * @return 处理
     */
    HttpResponse execute();
}