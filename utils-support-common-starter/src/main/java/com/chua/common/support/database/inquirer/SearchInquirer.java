package com.chua.common.support.database.inquirer;

import javax.sql.DataSource;

/**
 * 查询器
 * @author CH
 */
public class SearchInquirer {

    private DataSource dataSource;

    public SearchInquirer(DataSource dataSource) {
        this.dataSource = dataSource;
    }


}
