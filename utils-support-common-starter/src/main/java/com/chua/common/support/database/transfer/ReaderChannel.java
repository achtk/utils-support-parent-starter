package com.chua.common.support.database.transfer;

import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.value.DataMapping;

/**
 * 输入
 * @author CH
 */
public interface ReaderChannel extends AutoCloseable{
    /**
     * sink
     * @param sinkConfig config
     * @return this
     */
    ReaderChannel register(SinkConfig sinkConfig);
    /**
     * dataMapping
     * @param dataMapping dataMapping
     * @return this
     */
    ReaderChannel register(DataMapping dataMapping);

    /**
     * 是否自动关闭
     * @param autoClose 是否自动关闭
     * @return this
     */
    ReaderChannel autoClose(boolean autoClose);
    /**
     * 输入数据
     * @param sinkTable 表
     */
    void read(SinkTable sinkTable);
}
