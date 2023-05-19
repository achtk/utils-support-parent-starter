package com.chua.common.support.protocol.server;

import java.util.Map;

/**
 * 模板
 *
 * @author CH
 */
public interface TemplateResolver {

    /**
     * 转换
     *
     * @param source 源码
     * @param param  参数
     * @return 结果
     */
    String resolve(String source, Map<String, Object> param);
}
