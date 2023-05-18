package com.chua.common.support.database.transfer.datasource;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.resolver.DataSourceMetadataResolver;
import com.chua.common.support.database.resolver.MetadataResolver;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.SafeBiConsumer;
import com.chua.common.support.value.DataMapping;
import com.chua.common.support.value.MapValue;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 输出数据
 *
 * @author CH
 */
@Slf4j
public final class DataSourceReaderChannel extends AbstractReaderChannel {

    private final JdbcInquirer jdbcInquirer;
    private final Dialect dialect;
    private Metadata<?> metadata;

    public DataSourceReaderChannel(DataSource dataSource, Metadata<?> metadata) {
        super(new ExportConfiguration(), null);
        this.metadata = metadata;
        this.jdbcInquirer = new JdbcInquirer(dataSource, true);
        this.dialect = Dialect.create(dataSource);
        MetadataResolver metadataResolver = new DataSourceMetadataResolver();
        MetadataExecutor metadataExecutor = metadataResolver.resolve(metadata, dialect);
        metadataExecutor.execute(dataSource, Action.UPDATE);

    }

    /**
     * 查询语句
     *
     * @return 查询语句
     */
    private String createInsertSql() {
        if (dataMapping.isEmpty()) {
            return metadata.getInsertSql((SafeBiConsumer<Integer, Column>) (integer, column) -> {
            });
        }
        return metadata.getInsertSql(dataMapping.getValueMapping());
    }


    /**
     * 参数
     *
     * @param dataMapping 映射
     * @param metadata    字段
     * @param mapValue    值
     * @return 参数
     */
    public static Object[] createArgs(DataMapping dataMapping, Metadata<?> metadata, MapValue mapValue) {
        if(null == dataMapping || null == metadata) {
            return dataMapping.transferFromValue(mapValue);
        }

        if (dataMapping.isEmpty()) {
            List<Column> column = metadata.getColumn();
            List<Object> tpl = new LinkedList<>();
            for (int i = 0; i < column.size(); i++) {
                Column column1 = column.get(i);
                if (column1.isPrimary()) {
                    continue;
                }
                tpl.add(mapValue.get(column1.getName()));
            }

            return tpl.toArray();
        }

        String[] valueMapping = dataMapping.getValueMapping();
        Object[] rs = new Object[valueMapping.length];
        for (int i = 0; i < valueMapping.length; i++) {
            String s = valueMapping[i];
            rs[i] = mapValue.get(s);

        }
        return rs;
    }

    @Override
    public void read(SinkTable sinkTable) {
        String insertSql = createInsertSql();
        sinkTable.flow(it -> {
            jdbcInquirer.insert(insertSql, createArgs(dataMapping, metadata, it), HashMap.class);
        });
    }

    @Override
    public void close() throws Exception {
        if(autoClose) {
            jdbcInquirer.close();
        }
    }
}
