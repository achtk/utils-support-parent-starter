//package com.chua.example.database;
//
//import com.chua.common.support.database.inquirer.bs.BeanSearcher;
//import com.chua.common.support.database.inquirer.bs.SearchResult;
//import com.chua.common.support.database.inquirer.bs.implement.DefaultBeanSearcher;
//import com.chua.common.support.database.inquirer.bs.implement.DefaultSqlExecutor;
//import com.chua.common.support.utils.MapUtils;
//import com.chua.example.support.utils.DataSourceUtils;
//
//import javax.sql.DataSource;
//import java.util.Map;
//
//public class ParamExample {
//
//    public static void main(String[] args) {
//        Map<String, Object> params = MapUtils.builder()
//                .orderBy(OssLog::getId, "asc")
//                .page(2, 10)
//                .build();
//        DataSource atguigudb = DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb"));
//        BeanSearcher beanSearcher = new DefaultBeanSearcher(new DefaultSqlExecutor(atguigudb));
//        SearchResult<OssLog> search = beanSearcher.search(OssLog.class, params);
//        System.out.println();
//    }
//}
