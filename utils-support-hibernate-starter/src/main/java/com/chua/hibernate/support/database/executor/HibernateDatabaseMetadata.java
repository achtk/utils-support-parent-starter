package com.chua.hibernate.support.database.executor;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Table;

import java.sql.*;
import java.util.*;

/**
 * @author Administrator
 */
public class HibernateDatabaseMetadata {

    private final Map tables = new HashMap();
    private final Set sequences = new HashSet();
    private final boolean extras;

    private DatabaseMetaData meta;

    private final String[] types;

    /**
     * @deprecated Use {@link #HibernateDatabaseMetadata(Connection, org.hibernate.dialect.Dialect, Configuration)} instead
     */
    @Deprecated
    public HibernateDatabaseMetadata(Connection connection, org.hibernate.dialect.Dialect dialect) throws SQLException {
        this(connection, dialect, null, true);
    }

    /**
     * @deprecated Use {@link #HibernateDatabaseMetadata(Connection, org.hibernate.dialect.Dialect, Configuration, boolean)} instead
     */
    @Deprecated
    public HibernateDatabaseMetadata(Connection connection, org.hibernate.dialect.Dialect dialect, boolean extras) throws SQLException {
        this(connection, dialect, null, extras);
    }

    public HibernateDatabaseMetadata(Connection connection, org.hibernate.dialect.Dialect dialect, Configuration config) throws SQLException {
        this(connection, dialect, config, true);
    }

    public HibernateDatabaseMetadata(Connection connection, org.hibernate.dialect.Dialect dialect, Configuration config, boolean extras)
            throws SQLException {
        meta = connection.getMetaData();
        this.extras = extras;
        initSequences(connection, dialect);
        if (config != null
                && ConfigurationHelper.getBoolean(AvailableSettings.ENABLE_SYNONYMS, config.getProperties(), false)) {
            types = new String[]{"TABLE", "VIEW", "SYNONYM"};
        } else {
            types = new String[]{"TABLE", "VIEW"};
        }
    }

    public HibernateTableMetadata getTableMetadata(String name, String schema, String catalog, boolean isQuoted) throws HibernateException {

        Object identifier = identifier(catalog, schema, name);
        HibernateTableMetadata table = (HibernateTableMetadata) tables.get(identifier);
        if (table != null) {
            return table;
        } else {

            try {
                ResultSet rs = null;
                try {
                    boolean b = (isQuoted && meta.storesUpperCaseQuotedIdentifiers())
                            || (!isQuoted && meta.storesUpperCaseIdentifiers());

                    boolean b1 = (isQuoted && meta.storesLowerCaseQuotedIdentifiers())
                            || (!isQuoted && meta.storesLowerCaseIdentifiers());

                    if ((isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
                        rs = meta.getTables(catalog, schema, name, types);
                    } else if (b) {
                        rs = meta.getTables(
                                catalog != null ? catalog.toUpperCase(Locale.ROOT) : null,
                                schema != null ? schema.toUpperCase(Locale.ROOT) : null,
                                name.toUpperCase(Locale.ROOT),
                                types
                        );
                    } else if (b1) {
                        rs = meta.getTables(
                                catalog != null ? catalog.toLowerCase(Locale.ROOT) : null,
                                schema != null ? schema.toLowerCase(Locale.ROOT) : null,
                                name.toLowerCase(Locale.ROOT),
                                types
                        );
                    } else {
                        rs = meta.getTables(catalog, schema, name, types);
                    }

                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if (name.equalsIgnoreCase(tableName)) {
                            table = new HibernateTableMetadata(rs, meta, extras);
                            tables.put(identifier, table);
                            return table;
                        }
                    }

                    return null;

                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            } catch (SQLException sqlException) {
                throw new HibernateException(sqlException);
            }
        }

    }

    private Object identifier(String catalog, String schema, String name) {
        return Table.qualify(catalog, schema, name);
    }

    private void initSequences(Connection connection, org.hibernate.dialect.Dialect dialect) throws SQLException {
        if (dialect.supportsSequences()) {
            String sql = dialect.getQuerySequencesString();
            if (sql != null) {

                Statement statement = null;
                ResultSet rs = null;
                try {
                    statement = connection.createStatement();
                    rs = statement.executeQuery(sql);

                    while (rs.next()) {
                        sequences.add(rs.getString(1).toLowerCase(Locale.ROOT).trim());
                    }
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
        }
    }

    public boolean isSequence(Object key) {
        if (key instanceof String) {
            String[] strings = StringHelper.split(".", (String) key);
            return sequences.contains(strings[strings.length - 1].toLowerCase(Locale.ROOT));
        }
        return false;
    }

    public boolean isTable(Object key) throws HibernateException {
        if (key instanceof String) {
            Table tbl = new Table((String) key);
            if (getTableMetadata(tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted()) != null) {
                return true;
            } else {
                String[] strings = StringHelper.split(".", (String) key);
                if (strings.length == 3) {
                    tbl = new Table(strings[2]);
                    tbl.setCatalog(strings[0]);
                    tbl.setSchema(strings[1]);
                    return getTableMetadata(tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted()) != null;
                } else if (strings.length == 2) {
                    tbl = new Table(strings[1]);
                    tbl.setSchema(strings[0]);
                    return getTableMetadata(tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted()) != null;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "DatabaseMetadata" + tables.keySet().toString() + sequences.toString();
    }
}
