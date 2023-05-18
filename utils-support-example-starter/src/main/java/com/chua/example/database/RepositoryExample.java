//
//package com.chua.example.database;
//
//import com.chua.common.support.database.datasource.DataSourceMarker;
//import com.chua.common.support.database.inquirer.bs.SearchResult;
//import com.chua.common.support.database.inquirer.bs.util.MapBuilder;
//import com.chua.common.support.database.repository.DataSourceRepositoryFactory;
//import com.chua.common.support.database.repository.MetadataRepository;
//import com.chua.common.support.depends.DependencyResolver;
//import com.chua.common.support.table.ConnectorMetadata;
//import com.chua.common.support.utils.MapUtils;
//import com.chua.example.support.pool.TestEntity;
//import com.chua.example.support.utils.DataSourceUtils;
//import com.chua.example.support.utils.MockUtils;
//
//@DependencyResolver
//public class RepositoryExample {
//
//
//    public static void main(String[] args) {
////        testDataSource();
//        testFile();
//
//    }
//
//    private static void testDataSource() {
//        DataSourceRepositoryFactory factory = new DataSourceRepositoryFactory(
//                DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb"))
//        );
//        MetadataRepository<OssLog> metadataRepository = factory.build(OssLog.class);
//
//
//        SearchResult<OssLog> query1 = metadataRepository.query(MapUtils.builder().orderBy(OssLog::getDataId).limit(0, 10));
//        SearchResult<OssLog> query12 = metadataRepository.query(MapUtils.builder().orderBy(OssLog::getDataId).limit(0, 20));
//        MapBuilder mapBuilder = MapUtils.builder().orderBy(OssLog::getDataId).limit(10, 10);
//        SearchResult<OssLog> query2 = metadataRepository.query(mapBuilder);
//        System.out.println(query1);
//        System.out.println(query2);
//        TestEntity entity = MockUtils.create(TestEntity.class);
//        OssLog ossLog = metadataRepository.findById(1838);
////        testEntity.setSuccess("234sdfsdf33");
////        TestEntity testEntity1 = metadataRepository.updateById(testEntity);
////        metadataRepository.save(entity);
////        metadataRepository.saveBatch(MockUtils.createForList(TestEntity.class));
////        boolean remove = metadataRepository.removeById(2);
//        System.out.println();
//    }
//
//    private static void testFile() {
//        DataSourceRepositoryFactory factory = new DataSourceRepositoryFactory(
//                DataSourceMarker.builder()
//                        .addMetadata(ConnectorMetadata.create("file").name("oss_log").file("file/1.xls"))
//                        .build().mark());
//        MetadataRepository<OssLog> metadataRepository = factory.build(OssLog.class);
//
//
//        SearchResult<OssLog> query1 = metadataRepository.query(MapUtils.builder().orderBy(OssLog::getDataId).limit(0, 10));
//        SearchResult<OssLog> query12 = metadataRepository.query(MapUtils.builder().orderBy(OssLog::getDataId).limit(0, 20));
//        MapBuilder mapBuilder = MapUtils.builder().orderBy(OssLog::getDataId).limit(10, 10);
//        SearchResult<OssLog> query2 = metadataRepository.query(mapBuilder);
//        System.out.println(query1);
//        System.out.println(query2);
////        TestEntity entity = MockUtils.create(TestEntity.class);
////        TestEntity testEntity = metadataRepository.getById(1);
////        testEntity.setSuccess("234sdfsdf33");
////        TestEntity testEntity1 = metadataRepository.updateById(testEntity);
////        metadataRepository.save(entity);
////        metadataRepository.saveBatch(MockUtils.createForList(TestEntity.class));
////        boolean remove = metadataRepository.removeById(2);
//        System.out.println();
//    }
//
//}
