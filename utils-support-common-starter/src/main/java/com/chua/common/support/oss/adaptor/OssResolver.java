package com.chua.common.support.oss.adaptor;

import com.chua.common.support.lang.page.Page;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.value.Value;

import java.io.OutputStream;

/**
 * 适配器
 *
 * @author CH
 */
public interface OssResolver extends AutoCloseable {

    @Override
    default void close() throws Exception {

    }

    /**
     * 预览
     *
     * @param ossSystem bucket
     * @param path      预览的文件路径
     * @param mode      模式. download/preview
     * @param range     区间(只有下载有效)
     * @param os        输出
     */
    void preview(OssSystem ossSystem, String path, Mode mode, Range<Long> range, OutputStream os);

    /**
     * 保存文件
     *
     * @param parentPath parentPath
     * @param bytes      文件
     * @param ossSystem  ossSystem
     * @param name       名称
     * @return 结果
     */
    Value<String> storage(String parentPath, byte[] bytes, OssSystem ossSystem, String name);

    /**
     * 子目录
     *
     * @param ossSystem 配置
     * @param id        配置ID
     * @param name      路径
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return node
     */
    Page<OssNode> getChildren(OssSystem ossSystem, String id, String name, Integer pageNum, Integer pageSize);

    /**
     * 删除对象
     *
     * @param ossSystem pei
     * @param id        id
     * @param name      name
     * @return 是否删除
     */
    Boolean deleteObject(OssSystem ossSystem, String id, String name);
}
