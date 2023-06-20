package com.chua.example.mapping;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.AutoMetadata;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.orm.conditions.Wrappers;
import com.chua.example.DataSourceUtils;
import com.chua.example.database.SimpleRepository;
import com.chua.example.pool.TestEntity;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author CH
 */
public class DatabaseMappingExample {

    public static void main(String[] args) {
        DataSource dataSource = DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("websql"));
        AutoMetadata autoMetadata = AutoMetadata.builder().suffix("20230315").build();
        MetadataExecutor metadataExecutor = autoMetadata.doExecute(TestEntity.class);

        metadataExecutor.execute(dataSource, Action.UPDATE);

        SimpleRepository<TestEntity> repository = autoMetadata.createRepository
                (dataSource, TestEntity.class, SimpleRepository.class);

        TestEntity testEntity = new TestEntity();
        testEntity.setSuccess("false");
        //SAVE
//        repository.save(testEntity);
        //saveBatch
//        repository.saveBatch(Lists.newArrayList(new TestEntity().setDevice(1), new TestEntity().setDevice(2)));
        //UPDATE
//        testEntity.setSuccess("true");
//        repository.updateById(testEntity);
        //DELETE
//        repository.deleteById(testEntity.getId());

        List<TestEntity> list1 = repository.list(Wrappers.<TestEntity>lambdaQuery()
                .gt(TestEntity::getId, 4)
                .lt(TestEntity::getId, 104));
        //custom sql
        List<TestEntity> list = repository.list(2, 4);
        System.out.println();
    }

}
