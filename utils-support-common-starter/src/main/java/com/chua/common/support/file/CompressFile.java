package com.chua.common.support.file;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.resource.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * 压缩文件
 *
 * @author CH
 */
public interface CompressFile<IO, Entry> {
    /**
     * 打印树
     *
     * @return 打印树
     */
    String printTree();

    /**
     * 遍历
     *
     * @param action 动作
     */
    void forEach(BiConsumer<InputStream, Entry> action);

    /**
     * 生成树
     *
     * @param tp   结果
     * @param name 名称
     */
    @SuppressWarnings("ALL")
    static void mergeTree(Map tp, String name) {
        List<String> strings = Splitter.on(SYMBOL_LEFT_SLASH).omitEmptyStrings().trimResults().splitToList(name);
        int size = strings.size();
        String last = null;
        Map tpl = null;
        for (int i = 0; i < size; i++) {
            String string = strings.get(i);
            last = strings.get(i);
            if (null == tpl) {
                tp.computeIfAbsent(string, it -> new LinkedHashMap<>());
                tpl = (Map) tp.get(string);
                continue;
            }

            tpl.computeIfAbsent(last, it -> new LinkedHashMap<>());
            tpl = (Map) tpl.get(last);
        }
    }

    /**
     * 压缩文件
     *
     * @param file 文件
     * @return 压缩文件
     */
    static CompressFile of(String file) {
        return (CompressFile) ResourceFileBuilder.builder().open(file);
    }

    /**
     * 压缩文件
     *
     * @param file 文件
     * @return 压缩文件
     */
    static CompressFile of(File file) {
        return (CompressFile) ResourceFileBuilder.builder().open(file);
    }

    /**
     * 解压
     *
     * @param folder       文件夹
     * @param deleteSource 是否删除源文件
     * @param pattern      表达式
     * @throws Exception ex
     */
    void unpack(String folder, boolean deleteSource, String pattern) throws Exception;

    /**
     * 压缩
     *
     * @param folder       文件夹
     * @param deleteSource 是否删除源文件
     * @param pattern      表达式
     * @throws Exception ex
     */
    void pack(String folder, boolean deleteSource, String pattern) throws Exception;

    /**
     * 文件列表
     *
     * @param pattern  表达式
     * @param consumer 消费者
     * @return 文件
     */
    List<Resource> list(String pattern, BiConsumer<IO, Entry> consumer);

    /**
     * 文件列表
     *
     * @param pattern 表达式
     * @return 文件
     */
    default List<Resource> list(String pattern) {
        return list(pattern, null);
    }

    /**
     * 打开文件
     *
     * @param name 名称
     * @return 流
     * @throws IOException ex
     */
    ByteSource openInputStream(String name) throws IOException;
}
