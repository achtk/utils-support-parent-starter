package com.chua.common.support.database.structure;

import com.chua.common.support.annotations.Spi;

import javax.sql.DataSource;

/**
 * mysql
 *
 * @author CH
 */
@Spi("mysql")
public class MysqlTableStructure extends AbstractTableStructure {

    public MysqlTableStructure(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    String strToColumnSql(String schema, String name) {
        return String.format("SELECT\n" +
                "\tt.*,\n" +
                "\tcase when ISNULL(column_comment) = false then CONCAT(t.column_type1, \" COMMENT '\", t.column_comment, \"'\" ) else t.column_type1 end column_type\n" +
                "FROM\n" +
                "\t(SELECT t.table_name, \n" +
                " c.column_name column_name, \n" +
                " c.data_type data_type, \n" +
                " c.COLUMN_KEY column_key, \n" +
                " c.column_comment column_comment, \n" +
                " c.numeric_precision numeric_precision, \n" +
                "\t\t c.numeric_scale numeric_scale, \n" +
                "\t\t \tcase c.COLUMN_KEY when 'PRI' then CONCAT(c.COLUMN_TYPE, ' NOT NULL ', c.EXTRA) else case c.iS_NULLABLE when 'NO' then CONCAT(c.COLUMN_TYPE,  ' NOT NULL ') else c.COLUMN_TYPE end   end column_type1, \n" +
                "\t\t IFNULL(CHARACTER_MAXIMUM_LENGTH,0) AS column_size \n" +
                "FROM INFORMATION_SCHEMA.TABLES AS t \n" +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS c ON\n" +
                "\t(t.table_name = c.table_name)\n" +
                "WHERE t.table_name = '%s' \n" +
                "AND t.table_schema = '%s' \n" +
                "ORDER BY t.table_name\t) t\n", name, schema);
    }

}
