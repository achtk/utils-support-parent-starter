package com.chua.common.support.database.transfer;

import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.value.DataMapping;

/**
 * 输出
 * @author CH
 */
public interface WriterChannel extends AutoCloseable{
    /**
     * sink
     * @param sinkConfig config
     * @return this
     */
    WriterChannel register(SinkConfig sinkConfig);
    /**
     * dataMapping
     * @param dataMapping dataMapping
     * @return this
     */
    WriterChannel register(DataMapping dataMapping);
    /**
     * 是否自动关闭
     * @param autoClose 是否自动关闭
     * @return this
     */
    WriterChannel autoClose(boolean autoClose);
    /**
     * 是否完成
     * @return 是否完成
     */
    boolean isFinish();
    /**
     * 输出数据
     * @return 输出数据
     */
    SinkTable createSinkTable();
}
