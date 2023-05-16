package com.chua.common.support.context.resolver;

/**
 * 初始化解析器
 *
 * @author CH
 */
public interface InitializingResolver {
    /**
     * 是否是子类
     *
     * @param bean 类型
     * @return 刷新
     */
    void refresh(Object bean);
}
