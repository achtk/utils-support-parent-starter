package com.chua.common.support.extra.quickio.api;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

/**
 * tin
 * @author Administrator
 */
public interface Tin extends AutoCloseable {
    /**
     * 关闭
     */
    @Override
    void close();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 保存
     * @param filename 文件名
     * @param file 文件
     */
    void put(String filename, File file);
    /**
     * 保存
     * @param filename 文件名
     * @param bytes 文件
     */
    void put(String filename, byte[] bytes);

    /**
     * 获取文件
     * @param filename 文件名
     * @return 文件
     */
    File get(String filename);

    /**
     * 删除文件
     * @param filename 文件名
     */
    void remove(String filename);

    /**
     * 查询文件
     * @return 文件
     */
    List<File> list();

    /**
     * 遍历文件
     * @param predicate 函数
     */
    void foreach(Predicate<File> predicate);
}
