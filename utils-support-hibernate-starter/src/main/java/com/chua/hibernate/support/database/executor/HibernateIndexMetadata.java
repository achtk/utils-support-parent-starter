package com.chua.hibernate.support.database.executor;

import org.hibernate.tool.hbm2ddl.ColumnMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class HibernateIndexMetadata {
    private final String name;
    private final List columns = new ArrayList();

    HibernateIndexMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("INDEX_NAME");
    }

    public String getName() {
        return name;
    }

    void addColumn(HibernateColumnMetadata column) {
        if (column != null) {
            columns.add(column);
        }
    }

    public ColumnMetadata[] getColumns() {
        return (ColumnMetadata[]) columns.toArray(new ColumnMetadata[0]);
    }

    public String toString() {
        return "IndexMatadata(" + name + ')';
    }
}
