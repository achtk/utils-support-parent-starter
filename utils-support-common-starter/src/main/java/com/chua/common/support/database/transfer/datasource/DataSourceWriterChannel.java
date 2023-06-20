package com.chua.common.support.database.transfer.datasource;

import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 输出数据
 * @author CH
 */
@Slf4j
public final class DataSourceWriterChannel extends AbstractWriterChannel {

    private final JdbcInquirer jdbcInquirer;
    private final Dialect dialect;
    private final Metadata<?> metadata;

    private final AtomicInteger count = new AtomicInteger(0);

    public DataSourceWriterChannel(DataSource dataSource, Metadata<?> metadata) {
        super(new ExportConfiguration(), null);
        this.metadata = metadata;
        this.jdbcInquirer = new JdbcInquirer(dataSource, true);
        this.dialect = Dialect.create(dataSource);
    }

    @Override
    public boolean isFinish() {
        return status.get();
    }

    @Override
    public SinkTable createSinkTable() {
        String sql = createSql();
        SqlModel sqlModel = dialect.formatPageSql(sql, count.get() == 0 ? sinkConfig.getOffset() : count.get(), sinkConfig.getLimit());
        List<Map<String, Object>> query = null;
        try {
            query = jdbcInquirer.query(sqlModel.getSql(), sqlModel.getArgs());
        } catch (Exception e) {
            log.warn("{}", e.getMessage());
            status.set(true);
        }

        if(null == query || query.isEmpty()) {
            status.set(true);
            return null;
        }

        count.addAndGet(query.size());

        SinkTable sinkTable = new SinkTable(dataMapping, query);
        sinkTable.setTableName(metadata.getTable());
        return sinkTable;
    }

    /**
     * 查询语句
     * @return 查询语句
     */
    private String createSql() {
        if(dataMapping.isEmpty()) {
            return metadata.getQuerySql("*");
        }
        return metadata.getQuerySql(dataMapping.getKeyMapping());
    }

    @Override
    public void close() throws Exception {
        if(autoClose) {
            jdbcInquirer.close();
        }
    }
}
