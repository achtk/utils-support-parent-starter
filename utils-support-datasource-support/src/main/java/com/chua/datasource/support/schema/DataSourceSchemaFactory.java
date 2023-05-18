package com.chua.datasource.support.schema;

import com.chua.common.support.table.ConnectorMetadata;
import com.chua.datasource.support.datasource.MariaDBSqlDialect;
import com.chua.datasource.support.datasource.MysqlFixSqlDialect;
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlDialectFactory;
import org.apache.calcite.sql.SqlDialectFactoryImpl;
import org.apache.calcite.sql.dialect.*;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

/**
 * db
 *
 * @author CH
 */
public class DataSourceSchemaFactory extends AbstractCalciteSchemaFactory {

    private Schema schema;

    private static final UnicodeSqlDialectFactory INSTANCE = new UnicodeSqlDialectFactory();

    private Map<String, Object> operator;

    static {
        Class<SqlDialectFactoryImpl> aClass = SqlDialectFactoryImpl.class;
        try {
            Field instance = aClass.getDeclaredField("INSTANCE");
            instance.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(instance, instance.getModifiers() & ~Modifier.FINAL);
            instance.set(null, INSTANCE);
        } catch (Exception ignored) {

        }
    }


    public DataSourceSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata);
    }

    @Override
    public Schema getSchema() {
        if (null == schema) {
            schema = JdbcSchema.create(
                    connectorMetadata.get("schemaPlus", SchemaPlus.class),
                    getSchemaName(),
                    connectorMetadata.get("dataSource", DataSource.class),
                    connectorMetadata.get("catalog", String.class),
                    connectorMetadata.get("schema", String.class)
            );
        }

        return schema;
    }


    @Override
    Map<String, Table> getTable() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        for (String tableName : getSchema().getTableNames()) {
            builder.put(tableName, getSchema().getTable(tableName));
        }
        return builder.build();
    }


    public static class UnicodeSqlDialectFactory extends SqlDialectFactoryImpl implements SqlDialectFactory {
        private final JethroDataSqlDialect.JethroInfoCache jethroCache =
                JethroDataSqlDialect.createCache();

        @Override
        public SqlDialect create(DatabaseMetaData databaseMetaData) {
            String databaseProductName;
            int databaseMajorVersion;
            int databaseMinorVersion;
            String databaseVersion;
            try {
                databaseProductName = databaseMetaData.getDatabaseProductName();
                databaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
                databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
                databaseVersion = databaseMetaData.getDatabaseProductVersion();
            } catch (SQLException e) {
                throw new RuntimeException("while detecting database product", e);
            }
            final String upperProductName =
                    databaseProductName.toUpperCase(Locale.ROOT).trim();
            final String quoteString = getIdentifierQuoteString(databaseMetaData);
            final NullCollation nullCollation = getNullCollation(databaseMetaData);
            final Casing unquotedCasing = getCasing(databaseMetaData, false);
            final Casing quotedCasing = getCasing(databaseMetaData, true);
            final boolean caseSensitive = isCaseSensitive(databaseMetaData);
            final SqlDialect.Context c = SqlDialect.EMPTY_CONTEXT
                    .withDatabaseProductName(databaseProductName)
                    .withDatabaseMajorVersion(databaseMajorVersion)
                    .withDatabaseMinorVersion(databaseMinorVersion)
                    .withDatabaseVersion(databaseVersion)
                    .withIdentifierQuoteString(quoteString)
                    .withUnquotedCasing(unquotedCasing)
                    .withQuotedCasing(quotedCasing)
                    .withCaseSensitive(caseSensitive)
                    .withNullCollation(nullCollation);
            switch (upperProductName) {
                case "ACCESS":
                    return new AccessSqlDialect(c);
                case "APACHE DERBY":
                    return new DerbySqlDialect(c);
                case "CLICKHOUSE":
                    return new ClickHouseSqlDialect(c);
                case "DBMS:CLOUDSCAPE":
                    return new DerbySqlDialect(c);
                case "EXASOL":
                    return new ExasolSqlDialect(c);
                case "HIVE":
                    return new HiveSqlDialect(c);
                case "INGRES":
                    return new IngresSqlDialect(c);
                case "INTERBASE":
                    return new InterbaseSqlDialect(c);
                case "JETHRODATA":
                    return new JethroDataSqlDialect(
                            c.withJethroInfo(jethroCache.get(databaseMetaData)));
                case "LUCIDDB":
                    return new LucidDbSqlDialect(c);
                case "ORACLE":
                    return new OracleSqlDialect(c);
                case "PHOENIX":
                    return new PhoenixSqlDialect(c);
                case "MARIADB":
                    return new MariaDBSqlDialect(c.withDataTypeSystem(MysqlSqlDialect.MYSQL_TYPE_SYSTEM));
                case "MYSQL (INFOBRIGHT)":
                    return new InfobrightSqlDialect(c);
                case "MYSQL":
                    return new MysqlFixSqlDialect(
                            c.withDataTypeSystem(MysqlSqlDialect.MYSQL_TYPE_SYSTEM));
                case "REDSHIFT":
                    return new RedshiftSqlDialect(
                            c.withDataTypeSystem(RedshiftSqlDialect.TYPE_SYSTEM));
                case "SNOWFLAKE":
                    return new SnowflakeSqlDialect(c);
                case "SPARK":
                    return new SparkSqlDialect(c);
                default:
                    break;
            }
            // Now the fuzzy matches.
            if (databaseProductName.startsWith("DB2")) {
                return new Db2SqlDialect(c);
            } else if (upperProductName.contains("FIREBIRD")) {
                return new FirebirdSqlDialect(c);
            } else if (databaseProductName.startsWith("Informix")) {
                return new InformixSqlDialect(c);
            } else if (upperProductName.contains("NETEZZA")) {
                return new NetezzaSqlDialect(c);
            } else if (upperProductName.contains("PARACCEL")) {
                return new ParaccelSqlDialect(c);
            } else if (databaseProductName.startsWith("HP Neoview")) {
                return new NeoviewSqlDialect(c);
            } else if (upperProductName.contains("POSTGRE")) {
                return new PostgresqlSqlDialect(
                        c.withDataTypeSystem(PostgresqlSqlDialect.POSTGRESQL_TYPE_SYSTEM));
            } else if (upperProductName.contains("SQL SERVER")) {
                return new MssqlSqlDialect(c);
            } else if (upperProductName.contains("SYBASE")) {
                return new SybaseSqlDialect(c);
            } else if (upperProductName.contains("TERADATA")) {
                return new TeradataSqlDialect(c);
            } else if (upperProductName.contains("HSQL")) {
                return new HsqldbSqlDialect(c);
            } else if (upperProductName.contains("H2")) {
                return new H2SqlDialect(c);
            } else if (upperProductName.contains("VERTICA")) {
                return new VerticaSqlDialect(c);
            } else if (upperProductName.contains("SNOWFLAKE")) {
                return new SnowflakeSqlDialect(c);
            } else if (upperProductName.contains("SPARK")) {
                return new SparkSqlDialect(c);
            } else {
                return new AnsiSqlDialect(c);
            }
        }

        private static String getIdentifierQuoteString(DatabaseMetaData databaseMetaData) {
            try {
                return databaseMetaData.getIdentifierQuoteString();
            } catch (SQLException e) {
                throw new IllegalArgumentException("cannot deduce identifier quote string", e);
            }
        }

        private static NullCollation getNullCollation(DatabaseMetaData databaseMetaData) {
            try {
                if (databaseMetaData.nullsAreSortedAtEnd()) {
                    return NullCollation.LAST;
                } else if (databaseMetaData.nullsAreSortedAtStart()) {
                    return NullCollation.FIRST;
                } else if (databaseMetaData.nullsAreSortedLow()) {
                    return NullCollation.LOW;
                } else if (databaseMetaData.nullsAreSortedHigh()) {
                    return NullCollation.HIGH;
                } else if (isBigQuery(databaseMetaData)) {
                    return NullCollation.LOW;
                } else {
                    throw new IllegalArgumentException("cannot deduce null collation");
                }
            } catch (SQLException e) {
                throw new IllegalArgumentException("cannot deduce null collation", e);
            }
        }

        private static boolean isBigQuery(DatabaseMetaData databaseMetaData)
                throws SQLException {
            return "Google Big Query"
                    .equals(databaseMetaData.getDatabaseProductName());
        }
    }

    private static Casing getCasing(DatabaseMetaData databaseMetaData, boolean quoted) {
        try {
            if (quoted
                    ? databaseMetaData.storesUpperCaseQuotedIdentifiers()
                    : databaseMetaData.storesUpperCaseIdentifiers()) {
                return Casing.TO_UPPER;
            } else if (quoted
                    ? databaseMetaData.storesLowerCaseQuotedIdentifiers()
                    : databaseMetaData.storesLowerCaseIdentifiers()) {
                return Casing.TO_LOWER;
            } else if (quoted
                    ? (databaseMetaData.storesMixedCaseQuotedIdentifiers()
                    || databaseMetaData.supportsMixedCaseQuotedIdentifiers())
                    : (databaseMetaData.storesMixedCaseIdentifiers()
                    || databaseMetaData.supportsMixedCaseIdentifiers())) {
                return Casing.UNCHANGED;
            } else {
                return Casing.UNCHANGED;
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("cannot deduce casing", e);
        }
    }

    private static boolean isCaseSensitive(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.supportsMixedCaseIdentifiers()
                    || databaseMetaData.supportsMixedCaseQuotedIdentifiers();
        } catch (SQLException e) {
            throw new IllegalArgumentException("cannot deduce case-sensitivity", e);
        }
    }
}
