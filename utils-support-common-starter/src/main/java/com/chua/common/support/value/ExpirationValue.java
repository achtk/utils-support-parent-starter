package com.chua.common.support.value;

/**
 * 过期
 *
 * @author Administrator
 * @param <T> 类型
 */
public interface ExpirationValue<T> extends Value<T> {
    /**
     * 校验是否失效
     *
     * @return true 已失效 | false 未失效
     */
    boolean isExpiration();

    /**
     * 重置时间
     *
     * @return 重置时间
     */
    boolean resetExpire();
}
