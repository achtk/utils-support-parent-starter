package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 代理插件
 *
 * @author CH
 */
public interface ProxyParameterPlugin extends ProxyPlugin{
    /**
     * 执行结果
     *
     * @param index             索引
     * @param parameterDescribe 字段描述
     * @param arg               参数
     * @return 结果
     */
    Object proxy(int index, ParameterDescribe parameterDescribe, Object arg);

}
