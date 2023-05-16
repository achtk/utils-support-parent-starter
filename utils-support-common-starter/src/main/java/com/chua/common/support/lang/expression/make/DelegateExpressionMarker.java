package com.chua.common.support.lang.expression.make;


import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.lang.expression.listener.Listener;

/**
 * 对象生成器
 *
 * @author CH
 */
@SpiDefault
public class DelegateExpressionMarker implements ExpressionMarker {

    @Override
    public Object createObject(Listener listener, ClassLoader classLoader, Object[] args) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return null;
    }
}
