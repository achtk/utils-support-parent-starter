package com.chua.common.support.database;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * sql model
 *
 * @author CH
 */
@Data
@AllArgsConstructor
public final class SqlModel {

    public SqlModel(String sql, Object... args) {
        this.sql = sql;
        this.args.addAll(Arrays.asList(args));
    }

    /**
     * sql
     */
    private String sql;
    /**
     * 参数
     */
    private List<Object> args = new LinkedList<>();
}
