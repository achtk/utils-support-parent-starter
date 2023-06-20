
package com.chua.common.support.database.jdbc.id;

import com.chua.common.support.database.jdbc.Dialect;
import com.chua.common.support.database.jdbc.JdbcUtil;
import com.chua.common.support.database.jdbc.Type;

import java.sql.Connection;

/**
 * The platform-independent table model
 *
 * @author Yong Zhu
 * @since 1.0.2
 */
public class TableIdGenerator implements IdGenerator {

    /**
     * A unique generator name that can be referenced by one or more classes to be
     * the generator for id values.
     */
    private String name;

    /**
     * Table name in database
     */
    private String table;

    /**
     * Name of the primary key column in the table.
     */
    private String pkColumnName = "";

    /**
     * Name of the column that stores the last value generated.
     */
    private String valueColumnName = "";

    /**
     * The primary key value in the generator table that distinguishes this set of
     * generated values from others that may be stored in the table.
     */
    private String pkColumnValue = "";

    /**
     * The initial value to be used when allocating id numbers from the generator.
     */
    private Integer initialValue = 0;

    /**
     * The amount to increment by when allocating id numbers from the generator.
     */
    private Integer allocationSize = 50;

    public TableIdGenerator() {
        super();
    }

    public TableIdGenerator(String name, String table, String pkColumnName, String valueColumnName,
                            String pkColumnValue, Integer initialValue, Integer allocationSize) {
        this.name = name;
        this.table = table;
        this.pkColumnName = pkColumnName;
        this.valueColumnName = valueColumnName;
        this.pkColumnValue = pkColumnValue;
        this.initialValue = initialValue;
        this.allocationSize = allocationSize;
    }

    @Override
    public IdGenerator newCopy() {
        return new TableIdGenerator(name, table, pkColumnName, valueColumnName, pkColumnValue, initialValue,
                allocationSize);
    }

    @Override
    public GenerationType getGenerationType() {
        return GenerationType.TIMESTAMP;
    }

    @Override
    public String getIdGenName() {
        return name;
    }

    @Override
    public Boolean dependOnAutoIdGenerator() {
        return false;
    }

    /**
     * Get the next Table Generator ID
     */
    @Override
    public Object getNextID(Connection con, Dialect dialect, Type dataType) {
        int countOfRec = ((Number) JdbcUtil // NOSONAR
                .qryOneObject(con, "select count(*) from " + table + " where " + pkColumnName + "=?", pkColumnValue))
                .intValue();
        if (countOfRec == 0) {
            JdbcUtil.execute(con, "insert into " + table + "( " + pkColumnName + "," + valueColumnName + " )  values(?,?)",
                    pkColumnValue, initialValue);
            return initialValue;
        } else {
            JdbcUtil.execute(con, "update " + table + " set " + valueColumnName + "=" + valueColumnName + "+" + allocationSize
                    + "  where " + pkColumnName + " =?", pkColumnValue);

            int last = ((Number) JdbcUtil.qryOneObject(con, // NOSONAR
                    "select " + valueColumnName + " from " + table + " where " + pkColumnName + "=?", pkColumnValue))
                    .intValue();
            return last;
        }
    }

    // getter && setter=====================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    public String getPkColumnValue() {
        return pkColumnValue;
    }

    public void setPkColumnValue(String pkColumnValue) {
        this.pkColumnValue = pkColumnValue;
    }

    public Integer getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }

    public Integer getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(Integer allocationSize) {
        this.allocationSize = allocationSize;
    }
}
