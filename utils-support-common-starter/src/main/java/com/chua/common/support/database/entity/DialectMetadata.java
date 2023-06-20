package com.chua.common.support.database.entity;

import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import javax.sql.DataSource;

/**
 * 方言媒体
 *
 * @author Administrator
 */
@Data
public class DialectMetadata implements InitializingAware {

    private Dialect dialect;

    private Metadata<?> metadata;

    private DataSource dataSource;

    public DialectMetadata(DataSource dataSource, Metadata<?> metadata) {
        this.dataSource = dataSource;
        this.dialect = Dialect.create(dataSource);
        this.metadata = metadata;
    }

    public DialectMetadata(Dialect dialect, Metadata<?> metadata, DataSource dataSource) {
        this.dialect = dialect;
        this.metadata = metadata;
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        if(null == dialect || null == metadata || null == dataSource) {
            throw new NullPointerException("dialect/metadata/dataSource都不允许为空");
        }
    }

    /**
     * 获取表
     * @return 表
     */
    public String getTable() {
        return StringUtils.defaultString(metadata.getDatabase(), "") + "." + metadata.getTable();
    }
}
