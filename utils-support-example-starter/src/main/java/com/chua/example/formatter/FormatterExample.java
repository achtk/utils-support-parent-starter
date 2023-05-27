package com.chua.example.formatter;

import com.chua.common.support.lang.formatter.DmlFormatter;
import com.chua.common.support.lang.formatter.SqlFormatter;

public class FormatterExample {

    public static void main(String[] args) {
        String sql = "select * from user u left join role r on u.role = r.id";
        DmlFormatter dmlFormatter = new DmlFormatter();
        System.out.println(dmlFormatter.format(sql));
        System.out.println(SqlFormatter.format(sql));
    }
}
