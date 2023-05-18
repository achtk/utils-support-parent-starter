/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.chua.common.support.database.jdbc.model;

import com.chua.common.support.database.jdbc.DialectException;
import com.chua.common.support.database.jdbc.StrUtils;
import com.chua.common.support.database.jdbc.Type;
import com.chua.common.support.database.jdbc.id.GenerationType;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

/**
 * A ColumnModel definition represents a platform dependent column in a Database
 * Table, from 1.0.5 this class name changed from "Column" to "ColumnModel" to
 * avoid naming conflict to JPA's "@Column" annotation
 *
 * </pre>
 *
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
@Data
public class ColumnModel {
    private String columnName;

    private TableModel tableModel;

    private Type columnType;

    private String columnDefinition;

    private Boolean pkey = false;

    private Boolean nullable = true;

    /**
     * DDL check string
     */
    private String check;

    /**
     * DDL default value
     */
    private String defaultValue;

    /**
     * Optional, put an extra tail String at end of column definition DDL
     */
    private String tail;

    /**
     * Optional, Comment of this column
     */
    private String comment;

    boolean createTimestamp;

    boolean updateTimestamp;

    boolean createdBy;

    boolean LastModifiedBy;

    // =======================================================================
    private GenerationType idGenerationType;

    private String idGeneratorName;
    // =======================================================================

    // =====Below fields are designed only for ORM tools ==========
    /**
     * If not equal null, means this column has a converter class to translate field
     * value to column value or reverse. Note: @Version and @Enumerated annotated
     * will also be recorded as converterClass in this field
     */
    private Object converterClassOrName;

    /**
     * Map to a Java entity field, for DDL and ORM tool use
     */
    private String entityField;

    /**
     * The column length, for DDL and ORM tool use, defalut is 255
     */
    private Integer length = 255;

    /**
     * The numeric precision, for DDL and ORM tool use
     */
    private Integer precision = 0;

    /**
     * The numeric scale, for DDL and ORM tool use
     */
    private Integer scale = 0;

    /**
     * If insert-able or not, for ORM tool use only
     */
    private Boolean insertable = true;

    /**
     * If update-able or not, for ORM tool use only
     */
    private Boolean updatable = true;

    /**
     * If this is a Transient type, for ORM tool use only
     */
    private Boolean transientable = false;

    /**
     * ShardTable strategy and parameters, for ORM tool use only
     */
    private String[] shardTable = null;

    /**
     * ShardDatabase strategy and parameters, for ORM tool use only
     */
    private String[] shardDatabase = null;

    /**
     * the value of column, designed to ORM tool use
     */
    private Object value;

    /**
     * If column value exist , designed to ORM tool use
     */
    private Boolean valueExist = false;

    public ColumnModel(String columnName) {
        if (StrUtils.isEmpty(columnName))
            DialectException.throwEX("columnName is not allowed empty");
        this.columnName = columnName;
    }

    /**
     * Add a not null DDL piece if support
     */
    public ColumnModel notNull() {
        this.nullable = false;
        return this;
    }

    /**
     * Add a column check DDL piece if support
     */
    public ColumnModel check(String check) {
        this.check = check;
        return this;
    }

    public ColumnModel newCopy() {
        ColumnModel col = new ColumnModel(columnName);
        col.columnType = columnType;
        col.pkey = pkey.booleanValue();
        col.nullable = nullable;
        col.check = check;
        col.defaultValue = defaultValue;
        col.tail = tail;
        col.comment = comment;
        col.entityField = entityField;
        col.length = length;
        col.precision = precision;
        col.scale = scale;
        col.insertable = insertable;
        col.updatable = updatable;
        col.transientable = transientable;
        col.idGeneratorName = idGeneratorName;
        col.idGenerationType = idGenerationType;
        col.shardTable = shardTable;
        col.shardDatabase = shardDatabase;
        col.converterClassOrName = converterClassOrName;
        col.value = value;
        col.valueExist = valueExist;
        col.columnDefinition = columnDefinition;
        col.createTimestamp = createTimestamp;
        col.updateTimestamp = updateTimestamp;
        col.createdBy = createdBy;
        col.LastModifiedBy = LastModifiedBy;
        return col;
    }

    /**
     * A shortcut method to add a index for single column, for multiple columns
     * index please use tableModel.index() method
     */
    public ColumnModel singleIndex(String indexName) {
        makeSureTableModelExist();
        DialectException.assureNotEmpty(indexName, "indexName can not be empty");
        this.tableModel.index(indexName).columns(this.getColumnName());
        return this;
    }

    /**
     * A shortcut method to add a index for single column, for multiple columns
     * index please use tableModel.index() method
     */
    public ColumnModel singleIndex() {
        makeSureTableModelExist();
        this.tableModel.index().columns(this.getColumnName());
        return this;
    }

    /**
     * A shortcut method to add a unique constraint for single column, for multiple
     * columns index please use tableModel.unique() method
     */
    public ColumnModel singleUnique(String uniqueName) {
        makeSureTableModelExist();
        DialectException.assureNotEmpty(uniqueName, "indexName can not be empty");
        this.tableModel.unique(uniqueName).columns(this.getColumnName());
        return this;
    }

    /**
     * A shortcut method to add a unique constraint for single column, for multiple
     * columns index please use tableModel.unique() method
     */
    public ColumnModel singleUnique() {
        makeSureTableModelExist();
        this.tableModel.unique().columns(this.getColumnName());
        return this;
    }

    private void makeSureTableModelExist() {
        DialectException.assureNotNull(this.tableModel,
                "ColumnModel should belong to a TableModel, please call tableModel.column() method first.");
    }

    /**
     * Default value for column's definition DDL
     */
    public ColumnModel defaultValue(String value) {
        this.defaultValue = value;
        return this;
    }

    /**
     * Add comments at end of column definition DDL
     */
    public ColumnModel comment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Mark primary key, if more than one will build compound Primary key
     */
    public ColumnModel pkey() {
        this.pkey = true;
        return this;
    }

    /**
     * Mark is a shartTable column, for ORM tool use
     */
    public ColumnModel shardTable(String... shardTable) {
        this.shardTable = shardTable;
        return this;
    }

    /**
     * Mark is a shartDatabase column, for ORM tool use
     */
    public ColumnModel shardDatabase(String... shardDatabase) {
        this.shardDatabase = shardDatabase;
        return this;
    }

    /**
     * equal to pkey method. Mark primary key, if more than one will build compound
     * Primary key
     */
    public ColumnModel id() {
        this.pkey = true;
        return this;
    }

    /**
     * A shortcut method to add Foreign constraint for single column, for multiple
     * columns please use tableModel.fkey() method instead
     */
    public FKeyModel singleFKey(String... refTableAndColumns) {
        makeSureTableModelExist();
        if (refTableAndColumns == null || refTableAndColumns.length > 2)
            throw new DialectException(
                    "singleFKey() first parameter should be table name, second parameter(optional) should be column name");
        return this.tableModel.fkey().columns(this.columnName).refs(refTableAndColumns);
    }

    /**
     * The value of this column will be generated by a sequence
     */
    public ColumnModel sequenceGenerator(String name, String sequenceName, Integer initialValue,
                                         Integer allocationSize) {
        makeSureTableModelExist();
        this.tableModel.sequenceGenerator(name, sequenceName, initialValue, allocationSize);
        this.idGenerationType = GenerationType.SEQUENCE;
        this.idGeneratorName = name;
        return this;
    }

    /**
     * The value of this column will be generated by a sequence or table generator
     */
    public ColumnModel idGenerator(String idGeneratorName) {
        makeSureTableModelExist();
        this.idGenerationType = null;
        this.idGeneratorName = idGeneratorName;
        return this;
    }

    public ColumnModel tableGenerator(String name, String tableName, String pkColumnName, String valueColumnName,
                                      String pkColumnValue, Integer initialValue, Integer allocationSize) {
        makeSureTableModelExist();
        this.tableModel.tableGenerator(name, tableName, pkColumnName, valueColumnName, pkColumnValue, initialValue,
                allocationSize);
        this.idGenerationType = GenerationType.TABLE;
        this.idGeneratorName = name;
        return this;
    }

    // ===================================================

    /**
     * Put an extra tail String manually at the end of column definition DDL
     */
    public ColumnModel tail(String tail) {
        this.tail = tail;
        return this;
    }

    /**
     * Mark this column map to a Java entity field, if exist other columns map to
     * this field, delete other columns. This method only designed for ORM tool
     */
    public ColumnModel entityField(String entityFieldName) {
        DialectException.assureNotEmpty(entityFieldName, "entityFieldName can not be empty");
        this.entityField = entityFieldName;
        if (this.tableModel != null) {
            List<ColumnModel> oldColumns = this.tableModel.getColumns();
            Iterator<ColumnModel> columnIter = oldColumns.iterator();
            while (columnIter.hasNext()) {
                ColumnModel column = columnIter.next();
                if (entityFieldName.equals(column.getEntityField())
                        && !this.getColumnName().equals(column.getColumnName()))
                    columnIter.remove();
            }
        }
        return this;
    }

    /**
     * Mark a field insertable=true, only for JPA or ORM tool use
     */
    public ColumnModel insertable(Boolean insertable) {
        this.insertable = insertable;
        return this;
    }

    /**
     * Mark a field updatable=true, only for JPA or ORM tool use
     */
    public ColumnModel updatable(Boolean updatable) {
        this.updatable = updatable;
        return this;
    }

    public void checkReadOnly() {
        if (tableModel != null && tableModel.getReadOnly())
            throw new DialectException(
                    "TableModel '" + tableModel.getTableName() + "' is readOnly, can not be modified.");
    }

    //@formatter:off shut off eclipse's formatter
    public ColumnModel LONG() {
        this.columnType = Type.BIGINT;
        return this;
    }

    public ColumnModel BOOLEAN() {
        this.columnType = Type.BOOLEAN;
        return this;
    }

    public ColumnModel DOUBLE() {
        this.columnType = Type.DOUBLE;
        return this;
    }

    public ColumnModel INTEGER() {
        this.columnType = Type.INTEGER;
        return this;
    }

    public ColumnModel SHORT() {
        this.columnType = Type.SMALLINT;
        return this;
    }

    public ColumnModel DATE() {
        this.columnType = Type.DATE;
        return this;
    }

    public ColumnModel TIME() {
        this.columnType = Type.TIME;
        return this;
    }

    public ColumnModel TIMESTAMP() {
        this.columnType = Type.TIMESTAMP;
        return this;
    }

    public ColumnModel BIGINT() {
        this.columnType = Type.BIGINT;
        return this;
    }

    public ColumnModel BIT() {
        this.columnType = Type.BIT;
        return this;
    }

    public ColumnModel JAVA_OBJECT() {
        this.columnType = Type.JAVA_OBJECT;
        return this;
    }

    public ColumnModel NCLOB() {
        this.columnType = Type.NCLOB;
        return this;
    }

    public ColumnModel REAL() {
        this.columnType = Type.REAL;
        return this;
    }

    public ColumnModel SMALLINT() {
        this.columnType = Type.SMALLINT;
        return this;
    }

    public ColumnModel TINYINT() {
        this.columnType = Type.TINYINT;
        return this;
    }

    public ColumnModel LONGNVARCHAR(Integer length) {
        this.columnType = Type.LONGNVARCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel NCHAR(Integer length) {
        this.columnType = Type.NCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel NVARCHAR(Integer length) {
        this.columnType = Type.NVARCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel STRING(Integer length) {
        this.columnType = Type.VARCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel VARCHAR(Integer length) {
        this.columnType = Type.VARCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel FLOAT() {
        this.columnType = Type.FLOAT;
        return this;
    }

    public ColumnModel FLOAT(Integer precision) {
        this.columnType = Type.FLOAT;
        this.precision = precision;
        return this;
    }

    public ColumnModel BIGDECIMAL() {
        this.columnType = Type.NUMERIC;
        return this;
    }

    public ColumnModel BIGDECIMAL(Integer digiLength) {
        this.columnType = Type.NUMERIC;
        this.length = digiLength;
        return this;
    }

    public ColumnModel BIGDECIMAL(Integer precision, Integer scale) {
        this.columnType = Type.NUMERIC;
        this.precision = precision;
        this.scale = scale;
        return this;
    }

    public ColumnModel DECIMAL() {
        this.columnType = Type.DECIMAL;
        return this;
    }

    public ColumnModel DECIMAL(Integer precision, Integer scale) {
        this.columnType = Type.DECIMAL;
        this.precision = precision;
        this.scale = scale;
        return this;
    }

    public ColumnModel BINARY() {
        this.columnType = Type.BINARY;
        return this;
    }

    public ColumnModel BINARY(Integer length) {
        this.columnType = Type.BINARY;
        this.length = length;
        return this;
    }

    public ColumnModel BLOB() {
        this.columnType = Type.BLOB;
        return this;
    }

    public ColumnModel BLOB(Integer length) {
        this.columnType = Type.BLOB;
        this.length = length;
        return this;
    }

    public ColumnModel CHAR() {
        this.columnType = Type.CHAR;
        return this;
    }

    public ColumnModel CHAR(Integer length) {
        this.columnType = Type.CHAR;
        this.length = length;
        return this;
    }

    public ColumnModel CLOB() {
        this.columnType = Type.CLOB;
        return this;
    }

    public ColumnModel CLOB(Integer length) {
        this.columnType = Type.CLOB;
        this.length = length;
        return this;
    }

    public ColumnModel LONGVARBINARY() {
        this.columnType = Type.LONGVARBINARY;
        return this;
    }

    public ColumnModel LONGVARBINARY(Integer length) {
        this.columnType = Type.LONGVARBINARY;
        this.length = length;
        return this;
    }

    public ColumnModel LONGVARCHAR() {
        this.columnType = Type.LONGVARCHAR;
        return this;
    }

    public ColumnModel LONGVARCHAR(Integer length) {
        this.columnType = Type.LONGVARCHAR;
        this.length = length;
        return this;
    }

    public ColumnModel NUMERIC() {
        this.columnType = Type.NUMERIC;
        return this;
    }

    public ColumnModel NUMERIC(Integer digiLength) {
        this.columnType = Type.NUMERIC;
        this.length = digiLength;
        return this;
    }

    public ColumnModel NUMERIC(Integer precision, Integer scale) {
        this.columnType = Type.NUMERIC;
        this.precision = precision;
        this.scale = scale;
        return this;
    }

    public ColumnModel VARBINARY() {
        this.columnType = Type.VARBINARY;
        return this;
    }

    public ColumnModel VARBINARY(Integer length) {
        this.columnType = Type.VARBINARY;
        this.length = length;
        return this;
    }

    //@formatter:on

    public String getClearQuoteColumnName() {
        return StrUtils.clearQuote(columnName);
    }

}
