package com.chua.example.database;

import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.example.DataSourceUtils;

public class DatabaseExample {

    public static void main(String[] args) {
        JdbcInquirer jdbcInquirer = new JdbcInquirer(DataSourceUtils.createDefaultMysqlDataSource(""), true);

    }
}
