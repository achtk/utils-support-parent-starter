package com.chua.example.database;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.AutoMetadata;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.example.DataSourceUtils;
import com.chua.example.pool.TestEntity;

public class AutoCreateTableExample {

    public static void main(String[] args) {
        AutoMetadata autoMetadata = AutoMetadata.builder().suffix("20230315").build();
        MetadataExecutor metadataExecutor = autoMetadata.doExecute(TestEntity.class);
        metadataExecutor.execute(DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb")), Action.UPDATE);


        System.out.println();
    }
}
