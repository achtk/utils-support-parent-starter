package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.MapValue;
import com.chua.common.support.value.Pair;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * sql
 *
 * @author CH
 */
@Spi("sql")
public class SqlReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {

    private OutputStreamWriter writer;

    public SqlReaderChannel(Object obj) {
        super(obj);
    }


    @Override
    public void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                writer.write(createSql(sinkTable.getTableName(), mapValue));
            });
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建sql
     *
     * @param tableName 表名
     * @param mapping   数据
     * @param <T>       类型
     * @return 结果
     */
    private <T> String createSql(String tableName, MapValue mapping) {
        StringBuffer stringBuffer = new StringBuffer();
        Map<String, Pair> mapping1 = dataMapping.getMapping();
        List<Object> values = new LinkedList<>();
        List<Object> keys = new LinkedList<>();
        for (Map.Entry<String, Pair> entry : mapping1.entrySet()) {
            Object o = mapping.get(entry.getKey());
            keys.add(entry.getValue().getName());
            values.add(o);
        }
        stringBuffer.append("INSERT INTO ").append(tableName)
                .append("(`").append(Joiner.on("`,`").join(keys)).append("`)")
                .append(" VALUE ('").append(Joiner.on("','").join(values)).append("');\r\n");

        return stringBuffer.toString();
    }

    @Override
    public void afterPropertiesSet() {
        this.writer = createWriter();
    }

    @Override
    public void destroy() {
        try {
            writer.flush();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() throws Exception {
        if (autoClose) {
            IoUtils.closeQuietly(writer);
        }
    }
}
