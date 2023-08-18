package com.chua.agent.support.transfor;


import com.chua.agent.support.span.Span;

import java.util.List;

/**
 * 转化
 *
 * @author CH
 */
public interface Transfer {

    /**
     * 处理类
     *
     * @return 处理类
     */
    String name();

    /**
     * 转化
     *
     * @param params 参数
     * @param spans1 链路
     */
    void transfer(Object[] params, List<Span> spans1);
}
