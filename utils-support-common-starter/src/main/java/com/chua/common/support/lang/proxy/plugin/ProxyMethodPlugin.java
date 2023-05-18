package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 代理插件
 *
 * @author CH
 */
public interface ProxyMethodPlugin<D> extends ProxyPlugin{
    /**
     * 执行结果
     *
     * @param describe 描述
     * @param entity   对象
     * @param args     参数
     * @return 结果
     */
    Object execute(D describe, Object entity, Object[] args);

}
