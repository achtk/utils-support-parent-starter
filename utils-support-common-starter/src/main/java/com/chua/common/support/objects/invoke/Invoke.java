package com.chua.common.support.objects.invoke;

import com.chua.common.support.value.Value;

/**
 * 援引
 *
 * @author CH
 * @since 2023/09/04
 */
public interface Invoke {
    /**
     * 援引
     *
     * @param bean bean
     * @return {@link Object}
     */
    Value<?> invoke(Object bean);


    /**
     * 援引
     *
     * @return {@link Object}
     */
    default Value<?> invoke() {
        return invoke(null);
    }
}
