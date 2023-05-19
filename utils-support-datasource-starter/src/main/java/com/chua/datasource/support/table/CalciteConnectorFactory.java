package com.chua.datasource.support.table;

import com.chua.common.support.database.factory.DelegateDataSource;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.table.ConnectorFactory;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.common.support.table.SchemaFactory;
import com.chua.common.support.table.TableFactory;
import com.chua.common.support.utils.ClassUtils;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 表工厂
 *
 * @author CH
 */
public class CalciteConnectorFactory implements ConnectorFactory {
    private static final Set<String> DEFAULT_OPERATOR = Sets.newHashSet("SELECT");
    private static final Pattern EMPTY = Pattern.compile("\\s+");

    private final Connection connection;
    private CalciteConnection calciteConnection;
    private final SchemaPlus rootSchema;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> ADAPTOR_MAP = new ConcurrentHashMap<>();

    private final Properties info = new Properties();

    {
        info.put("lex", "MYSQL");
        info.put("fun", "mysql");
    }

    public CalciteConnectorFactory() {
        this.connection = createConnection();
        this.calciteConnection = (CalciteConnection) connection;
        this.rootSchema = ((CalciteConnection) connection).getRootSchema();
        initialAdaptor();
    }

    /**
     * 初始化adaptor
     */
    private void initialAdaptor() {
        ADAPTOR_MAP.putAll(ServiceProvider.of(TableFactory.class).mapping());
    }

    /**
     * 初始化
     */
    private Connection createConnection() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
            return connection.unwrap(CalciteConnection.class);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 注册表
     *
     * @param table 表
     */
    @Override
    @SuppressWarnings("ALL")
    public void register(ConnectorMetadata table) {
        String type = table.type();
        if (Strings.isNullOrEmpty(type)) {
            throw new IllegalArgumentException("type不能为空");
        }

        table.addParam("schemaPlus", rootSchema);
        TableFactory tableFactory = newTableFactory(type, table);

        if (null == tableFactory) {
            throw new IllegalArgumentException("当前不支持: " + type);
        }

        if (tableFactory instanceof SchemaFactory) {
            Schema schema1 = (Schema) ((SchemaFactory) tableFactory).getSchema();
            String subSchemaName = ((SchemaFactory) tableFactory).getSchemaName();
            rootSchema.add(subSchemaName, schema1);
            cache.put(subSchemaName, schema1);
        }

        Map<String, Table> tables = tableFactory.getTables();
        tables.forEach((n, t) -> {
            rootSchema.add(n, t);
            cache.put(n, t);
        });

    }

    @Override
    public DataSource getDataSource() {
        return new DelegateDataSource((Supplier<Connection>) () -> {
            CalciteConnection calciteConnection = (CalciteConnection) createConnection();
            SchemaPlus rootSchema1 = calciteConnection.getRootSchema();
            for (Map.Entry<String, Object> entry : cache.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Schema) {
                    rootSchema1.add(entry.getKey(), (Schema) value);
                    continue;
                }

                if (value instanceof Table) {
                    rootSchema1.add(entry.getKey(), (Table) value);
                }
            }
            return calciteConnection;
        });
    }

    /**
     * 显示表名
     *
     * @return 表名
     */
    @Override
    public Set<String> tableNames() {
        return rootSchema.getTableNames();
    }


    @Override
    public void close() throws Exception {
        if (null != calciteConnection) {
            calciteConnection.close();
        }

        if (null != connection) {
            connection.close();
        }
    }

    /**
     * 初始化
     *
     * @param type     类型
     * @param metadata
     * @return TableAdaptor
     */
    @SuppressWarnings("ALL")
    private TableFactory newTableFactory(String type, ConnectorMetadata metadata) {
        Class<?> tableAdaptor = ADAPTOR_MAP.get(type);
        if (null == tableAdaptor) {
            throw new IllegalArgumentException(type + " 表不存在");
        }
        return (TableFactory) ClassUtils.forObject(tableAdaptor, metadata);
    }

}
