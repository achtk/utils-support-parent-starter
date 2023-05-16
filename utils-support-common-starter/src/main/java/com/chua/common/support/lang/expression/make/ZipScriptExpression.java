package com.chua.common.support.lang.expression.make;

import com.chua.common.support.expression.listener.Listener;
import com.chua.common.support.spi.Spi;

/**
 * script
 *
 * @author CH
 */
@Spi({"zip", "jar", "war", "ear"})
public class ZipScriptExpression implements ExpressionMarker {

    @Override
    public Object createObject(Listener listener, ClassLoader classLoader, Object[] args) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return null;
    }
}
