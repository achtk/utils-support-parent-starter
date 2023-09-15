package com.chua.proxy.support.filter;


import org.apache.http.client.methods.Configurable;

/**
 * 过滤器插件
 *
 * @author CH
 */
public interface FilterPlugin extends Filter, Configurable {

    /**
     * 销毁
     */
    default void destroy() {
    }

    /**
     * 优先级
     *
     * @return int
     */
    default int getOrder() {
        return 0;
    }

}
