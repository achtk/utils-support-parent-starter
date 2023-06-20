package com.chua.common.support.database.inquirer;

import com.chua.common.support.database.entity.Column;

import javax.sql.DataSource;
import java.util.List;

/**
 * 查询器
 *
 * @author CH
 */
public interface Inquirer {

    /**
     * 数据源
     *
     * @return 数据源
     */
    DataSource getDataSource();

    /**
     * 执行command
     *
     * @param command command
     * @param args    参数
     * @return 结果
     * @throws Exception ex
     */
    int execute(String command, Object... args) throws Exception;

    /**
     * 获取字段
     * @param tableName 表名
     * @return 字段
     */
    List<Column> getColumn(String tableName);
}
