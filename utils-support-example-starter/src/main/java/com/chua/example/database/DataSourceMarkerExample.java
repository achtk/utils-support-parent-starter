//package com.chua.example.database;
//
//import com.chua.common.support.database.datasource.DataSourceMarker;
//import com.chua.common.support.table.ConnectorMetadata;
//import com.chua.example.support.utils.DataSourceUtils;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.commons.dbutils.handlers.MapListHandler;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//
//public class DataSourceMarkerExample {
//
//    public static void main(String[] args) throws SQLException {
//        DataSourceMarker.help();
//        OssLog ossLog1 = new OssLog("1", "1", "1", "1");
//        OssLog ossLog2 = new OssLog("2", "2", "2", "2");
//        DataSource dataSource = DataSourceMarker.builder()
////                .addMetadata(ConnectorMetadata.create("mem").name("ds").javaType(Holiday.class).addParam("data", MockUtils.createForList(Holiday.class, 10)))
////                .addMetadata(ConnectorMetadata.create("datasource").dataSource(DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb"))))
////                .addMetadata(ConnectorMetadata.create("tablesaw").name("saw")
////                        .addColumn("fileName", "VARCHAR")
////                        .addColumn("id", "VARCHAR")
////                        .addObjectData(ossLog1)
////                        .addObjectData(ossLog2))
////                .addMetadata(ConnectorMetadata.create("file").name("dbf")
////                        .mode("mem")
////                        .addMapping("序号", "id")
////                        .addMapping("文件", "fileName")
////                        .addMapping("url地址", "url")
////                        .addParam("directory", "z:/other/1.dbf"))
////                .addMetadata(ConnectorMetadata.create("file").name("excel")
////                        .mode("mem")
////                        .addMapping("序号", "id")
////                        .addMapping("文件", "fileName")
////                        .addMapping("url地址", "url")
////                        .addParam("directory", "z:/other/1.xlsx"))
//                .addMetadata(ConnectorMetadata.create("subtable")
//                        .addParam("table", "test_entity")
//                        .dataSource(DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb")))
//                        .strategy("sys_time"))
//                .build().mark();
//
//
//        QueryRunner queryRunner = new QueryRunner(dataSource);
//        List<Map<String, Object>> sublist00 = queryRunner.query("select * from test_entity where sys_time  >= '2023-03-15'", new MapListHandler());
////        List<Map<String, Object>> list00 = queryRunner.query("select * from oss_log limit 1 offset 1", new MapListHandler());
////        List<Map<String, Object>> list0 = queryRunner.query("select * from ds limit 3 offset 1", new MapListHandler());
////        System.out.println(list0);
////        List<Map<String, Object>> list1 = queryRunner.query("select * from saw where fileName = 1 or fileName > 2 ", new MapListHandler());
////        System.out.println(list1);
////        List<Map<String, Object>> list12 = queryRunner.query("select * from excel ", new MapListHandler());
////        List<Map<String, Object>> list13 = queryRunner.query("select * from dbf ", new MapListHandler());
////        List<Map<String, Object>> list14 = queryRunner.query("select * from dbf d full join excel e on d.url = e.url", new MapListHandler());
//        System.out.println();
//
//    }
//}
