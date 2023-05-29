package com.chua.common.support.http.render;

/**
 * 渲染器
 * @author CH
 */
public interface Render {

    /**
     * 序列化
     *
     * @param param       参数
     * @param contentType
     * @return 结果
     */
    byte[] render(Object param, String contentType);
}
