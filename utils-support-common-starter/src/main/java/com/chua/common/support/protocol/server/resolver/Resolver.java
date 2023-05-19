package com.chua.common.support.protocol.server.resolver;

/**
 * 解析器
 * @author CH
 */
public interface Resolver {

    /**
     * 解析器
     * @param obj 对象
     * @return 结果
     */
    byte[] resolve(Object obj);

    /**
     * 是否存在
     * @param obj 资源
     * @return 是否存在
     */
    default boolean hasResolve(Object obj) {
        return true;
    }
    /**
     * 类型
     * @return 类型
     */
    String getContentType();
}
