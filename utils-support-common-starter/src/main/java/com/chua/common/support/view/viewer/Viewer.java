package com.chua.common.support.view.viewer;

import com.chua.common.support.value.ContentTypeValue;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 试图解析器
 *
 * @author CH
 */
public interface Viewer {

    static final long MAX = 1024 * 1024 * 10L;

    /**
     * 解析试图
     *
     * @param inputStream 试图
     * @param os          输出
     * @param mode
     * @param uri         uri
     * @return 结果
     */
    ContentTypeValue<byte[]> resolve(InputStream inputStream, OutputStream os, String mode, String uri);
}
