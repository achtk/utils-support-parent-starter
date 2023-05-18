package com.chua.common.support.database.transfer.collection;

import com.chua.common.support.value.DataMapping;
import com.chua.common.support.value.MapValue;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 管道数据
 * @author CH
 */
@Data
public class SinkTable {

    private String tableName;
    private DataMapping dataMapping;
    private List<Map<String, Object>> query;

    public SinkTable(DataMapping dataMapping, List<Map<String, Object>> query) {
        this.dataMapping = dataMapping;
        this.query = query;
    }

    /**
     * 遍历
     * @param consumer 消费者
     */
    public void flow(Consumer<MapValue> consumer) {
        if(null == query) {
            return;
        }

        for (Map<String, Object> stringObjectMap : query) {
            consumer.accept(new MapValue(dataMapping, stringObjectMap));
        }
    }
}
