//package com.chua.example.database;
//
//import com.chua.example.support.utils.DataSourceUtils;
//import org.apache.calcite.adapter.jdbc.JdbcSchema;
//import org.apache.calcite.jdbc.CalciteConnection;
//import org.apache.calcite.schema.SchemaPlus;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.commons.dbutils.handlers.MapListHandler;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
///**
// * @author CH
// */
//public class SimpleExample {
//
//    public static void main(String[] args) throws SQLException {
//        Properties info = new Properties();
//        info.put("lex", "MYSQL");
//
//        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
//        SchemaPlus rootSchema = ((CalciteConnection) connection).getRootSchema();
//        JdbcSchema jdbcSchema = JdbcSchema.create(rootSchema, "atguigudb", DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb")), null, null);
//        Set<String> tableNames = jdbcSchema.getTableNames();
//        rootSchema.add("atguigudb", jdbcSchema);
//        for (String tableName : tableNames) {
//            rootSchema.add(tableName, jdbcSchema.getTable(tableName));
//        }
//
//        QueryRunner queryRunner = new QueryRunner();
//        List<Map<String, Object>> list00 = queryRunner.query(connection, "select * from oss_log", new MapListHandler());
//        System.out.println();
//    }
//}
