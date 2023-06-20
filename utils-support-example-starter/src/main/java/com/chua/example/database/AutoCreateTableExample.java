package com.chua.example.database;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.AutoMetadata;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.example.DataSourceUtils;
import com.chua.example.pool.TestEntity;

import javax.sql.DataSource;

public class AutoCreateTableExample {

    public static void main(String[] args) {
        DataSource dataSource = DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("websql"));
        AutoMetadata autoMetadata = AutoMetadata.builder().suffix("20230315").build();
        MetadataExecutor metadataExecutor = autoMetadata.doExecute(TestEntity.class);

        metadataExecutor.execute(dataSource, Action.UPDATE);
    }
}
