package com.chua.hibernate.support.database.executor;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.extract.spi.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 元数据执行器
 *
 * @author CH
 */
public class HibernateTableMetadata implements TableInformation {
    private final String catalog;
    private final String schema;
    private final String name;
    private final Map<String, HibernateColumnMetadata> columns = new HashMap<String, HibernateColumnMetadata>();
    private final Map<String, HibernateForeignKeyMetadata> foreignKeys = new HashMap<String, HibernateForeignKeyMetadata>();
    private final Map<String, HibernateIndexMetadata> indexes = new HashMap<String, HibernateIndexMetadata>();

    HibernateTableMetadata(ResultSet rs, DatabaseMetaData meta, boolean extras) throws SQLException {
        catalog = rs.getString("TABLE_CAT");
        schema = rs.getString("TABLE_SCHEM");
        name = rs.getString("TABLE_NAME");
        initColumns(meta);
        if (extras) {
            initForeignKeys(meta);
            initIndexes(meta);
        }
        String cat = catalog == null ? "" : catalog + '.';
        String schem = schema == null ? "" : schema + '.';
    }

    public QualifiedTableName getName() {
        return new QualifiedTableName(null, null, new Identifier(name, true));
    }

    @Override
    public boolean isPhysicalTable() {
        return true;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public ColumnInformation getColumn(Identifier columnIdentifier) {
        return columns.get(columnIdentifier.getCanonicalName());
    }

    @Override
    public PrimaryKeyInformation getPrimaryKey() {
        return null;
    }

    @Override
    public Iterable<ForeignKeyInformation> getForeignKeys() {
        return null;
    }

    @Override
    public ForeignKeyInformation getForeignKey(Identifier keyName) {
        HibernateForeignKeyMetadata metadata = foreignKeys.get(keyName.getCanonicalName());
        if (null == metadata) {
        }
        return null;
    }

    @Override
    public Iterable<IndexInformation> getIndexes() {
        return null;
    }

    @Override
    public IndexInformation getIndex(Identifier indexName) {
        return null;
    }

    @Override
    public void addColumn(ColumnInformation columnIdentifier) {

    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "TableMetadata(" + name + ')';
    }

    public HibernateColumnMetadata getColumnMetadata(String columnName) {
        return columns.get(columnName.toLowerCase(Locale.ROOT));
    }

    public HibernateForeignKeyMetadata getForeignKeyMetadata(String keyName) {
        return foreignKeys.get(keyName.toLowerCase(Locale.ROOT));
    }

    public HibernateForeignKeyMetadata getForeignKeyMetadata(ForeignKey fk) {
        for (HibernateForeignKeyMetadata existingFk : foreignKeys.values()) {
            if (existingFk.matches(fk)) {
                return existingFk;
            }
        }
        return null;
    }

    public HibernateIndexMetadata getIndexMetadata(String indexName) {
        return indexes.get(indexName.toLowerCase(Locale.ROOT));
    }

    private void addForeignKey(ResultSet rs) throws SQLException {
        String fk = rs.getString("FK_NAME");

        if (fk == null) {
            return;
        }

        HibernateForeignKeyMetadata info = getForeignKeyMetadata(fk);
        if (info == null) {
            info = new HibernateForeignKeyMetadata(rs);
            foreignKeys.put(info.getName().toLowerCase(Locale.ROOT), info);
        }

        info.addReference(rs);
    }

    private void addIndex(ResultSet rs) throws SQLException {
        String index = rs.getString("INDEX_NAME");

        if (index == null) {
            return;
        }

        HibernateIndexMetadata info = getIndexMetadata(index);
        if (info == null) {
            info = new HibernateIndexMetadata(rs);
            indexes.put(info.getName().toLowerCase(Locale.ROOT), info);
        }

        info.addColumn(getColumnMetadata(rs.getString("COLUMN_NAME")));
    }

    public void addColumn(ResultSet rs) throws SQLException {
        String column = rs.getString("COLUMN_NAME");

        if (column == null) {
            return;
        }

        if (getColumnMetadata(column) == null) {
            HibernateColumnMetadata info = new HibernateColumnMetadata(rs, this);
            columns.put(info.getName().toLowerCase(Locale.ROOT), info);
        }
    }

    private void initForeignKeys(DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getImportedKeys(catalog, schema, name);
            while (rs.next()) {
                addForeignKey(rs);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void initIndexes(DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getIndexInfo(catalog, schema, name, false, true);

            while (rs.next()) {
                if (rs.getShort("TYPE") == DatabaseMetaData.tableIndexStatistic) {
                    continue;
                }
                addIndex(rs);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void initColumns(DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getColumns(catalog, schema, name, "%");
            while (rs.next()) {
                addColumn(rs);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
