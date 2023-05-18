package com.chua.common.support.database.metadata;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 组装
 * @author CH
 */
@Getter
public class SparkMetadata implements Metadata<Object> {

    private String table;

    private String database;


    private List<Column> column;

    private String definition;

    private String prefix = "";

    private String suffix = "";

    private String tableComment;

    @Setter
    private Map<String, String> mapping = new LinkedHashMap<>();

    public SparkMetadata() {
    }

    public SparkMetadata(String suffix) {
        this.suffix = suffix;
    }

    public SparkMetadata(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getTable() {
        return prefix + table + suffix;
    }

    @Override
    public String getTableComment() {
        return tableComment;
    }

    @Override
    public Map<String, String> getMapping() {
        return mapping;
    }

    @Override
    public Metadata<Object> setTable(String tableName) {
        this.table = tableName;
        return this;
    }

    @Override
    public Metadata<Object> setDatabase(String databaseName) {
        this.database = databaseName;
        return this;
    }

    /**
     * 添加字段
     * @param column 字段
     * @return 结果
     */
    public SparkMetadata addColumn(Column column) {
        if(null == this.column) {
            synchronized (this) {
                if(null == this.column) {
                    this.column = new LinkedList<>();
                }
            }
        }
        this.column.add(column);
        return this;
    }

    @Override
    public String tableDefinition() {
        return definition;
    }

    @Override
    public String getPrimaryId() {
        for (Column column : column) {
            if(column.isPrimary()) {
                return column.getName();
            }
        }

        return null;
    }

    @Override
    public List<Index> getIndex() {
        return Collections.emptyList();
    }
}
