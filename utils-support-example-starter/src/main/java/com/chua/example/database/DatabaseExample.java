package com.chua.example.database;

import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.datasource.support.table.CalciteConnectorFactory;
import com.chua.example.DataSourceUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DatabaseExample {

    public static void main(String[] args) throws Exception {
        CalciteConnectorFactory factory = new CalciteConnectorFactory();
        factory.register(new File("Z:\\other\\xlsx2.xlsx"));
        JdbcInquirer jdbcInquirer = new JdbcInquirer(factory.getDataSource(), true);
        List<Map<String, Object>> query = jdbcInquirer.query("select * from xlsx2 where num = '21'");

    System.out.println();
    }
}
