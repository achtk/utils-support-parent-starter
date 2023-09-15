
package com.chua.proxy.support.context;


import com.chua.proxy.support.attribute.AttributesHolder;
import com.chua.proxy.support.exchange.Exchange;

/**
 * 请求上下文
 *
 * @author CH
 */
public interface RequestContext extends AttributesHolder {

    /**
     * 获取交换
     *
     * @return {@link Exchange}
     */
    Exchange getExchange();

}
