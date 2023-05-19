package com.chua.common.support.database.structure;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.database.Database;
import com.chua.common.support.database.inquirer.JdbcInquirer;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * structure
 *
 * @author CH
 */
public abstract class AbstractTableStructure implements TableStructure {

    private final DataSource dataSource;
    private static final Map<String, List<Structure>> CACHE = new ConcurrentReferenceHashMap<>(64);

    public AbstractTableStructure(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Structure> analysis(String schema, StructureValue structureValue) {
        Database database = Database.newBuilder().datasource(dataSource).build();
        JdbcInquirer jdbcInquirer = database.createJdbcInquirer();
        String key = schema + structureValue.getSource();

        List<Structure> ifPresent = CACHE.get(key);
        synchronized (this) {
            if (null == ifPresent) {
                try {
                    ifPresent = jdbcInquirer.query(strToColumnSql(schema, structureValue.getSource()), Structure.class);
                    CACHE.put(key, ifPresent);
                } catch (Exception e) {
                    CACHE.put(key, Collections.emptyList());
                }
            }

        }
        try {
            String createSql = createStructure(schema, ifPresent, structureValue.getTarget());
            jdbcInquirer.executeUpdate(createSql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 建表语句
     *
     * @param schema     数据库
     * @param structures 结构
     * @param table      目标
     * @return sql
     */
    private String createStructure(String schema, List<Structure> structures, String table) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE `").append(schema).append("`.`")
                .append(table)
                .append("` (\n");

        boolean hasPrimaryKey = false;
        for (Structure structure : structures) {
            stringBuffer.append("  `").append(structure.getColumnName()).append("` ").append(structure.getColumnType()).append("\n,");
            if ("PRI".equals(structure.getColumnKey())) {
                hasPrimaryKey = true;
            }
        }

        if (hasPrimaryKey) {
            stringBuffer.append("PRIMARY KEY (");
            for (Structure structure : structures) {
                if (!"PRI".equals(structure.getColumnKey())) {
                    continue;
                }
                stringBuffer.append("`").append(structure.getColumnName()).append("`,");
            }
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());

        stringBuffer.append(")\n)");
        return stringBuffer.toString();
    }

    /**
     * sql
     *
     * @param schema 数据库
     * @param name   名称
     * @return sql
     */
    abstract String strToColumnSql(String schema, String name);
}
