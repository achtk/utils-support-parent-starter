package com.chua.common.support.oss.handler;

import com.chua.common.support.lang.page.Page;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.OssSystem;

/**
 * 解析器
 * @author CH
 */
public interface OssHandler {
    /**
     * 深层解析
     *
     * @param ossSystem 配置
     * @param ossBucket bucket
     * @param path      解析的文件
     * @param name      匹配的名称
     * @param pageNum
     * @param pageSize
     * @return 结果
     */
    Page<OssNode> analysis(OssSystem ossSystem, String ossBucket, String path, String name, Integer pageNum, Integer pageSize);
}
