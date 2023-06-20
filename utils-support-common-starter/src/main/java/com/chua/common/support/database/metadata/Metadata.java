package com.chua.common.support.database.metadata;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static java.util.stream.Collectors.joining;

/**
 * 表信息
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public interface Metadata<T> {
    static Map<Class<?>, Metadata> CACHE = new ConcurrentHashMap<>();

    static <T> Metadata<Object> of(Class<T> entityClass) {
        return MapUtils.computeIfAbsent(CACHE, entityClass, new Function<Class<?>, Metadata>() {
            @Override
            public Metadata apply(Class<?> aClass) {
                return new DelegateMetadata(aClass, "", "");
            }
        });
    }

    /**
     * 表名
     *
     * @return 表名
     */
    String getTable();

    /**
     * 描述
     * @return 描述
     */
    String getTableComment();

    /**
     * 数据库名称
     *
     * @return 数据库名称
     */
    String getDatabase();

    /**
     * 数据库名称
     *
     * @return 数据库名称
     */
    String getCatalog();

    /**
     * 字段
     *
     * @return 字段
     */
    List<Column> getColumn();

    /**
     * 映射
     * @return 映射
     */
    Map<String, String> getMapping();

    /**
     * 表名
     *
     * @param tableName 表名
     * @return this
     */
    Metadata<T> setTable(String tableName);

    /**
     * 数据库名称
     *
     * @param databaseName 数据库名称
     * @return this
     */
    Metadata<T> setDatabase(String databaseName);

    /**
     * 字段
     *
     * @param column 字段
     * @return this
     */
    Metadata<T> addColumn(Column column);

    /**
     * java类型
     *
     * @return 类型
     */
    default Class<?> getJavaType() {
        return Object.class;
    }

    /**
     * 是否匹配
     *
     * @param metadata 元数据
     * @return 是否匹配
     */
    default boolean isMatch(Metadata<T> metadata) {
        String database = getDatabase();
        String database1 = metadata.getDatabase();
        if (!StringUtils.isNullOrEmpty(database) && !StringUtils.isNullOrEmpty(database1)) {
            if (!database.equals(database1)) {
                return false;
            }
            return getTable().toUpperCase().equals(metadata.getTable().toUpperCase());
        }
        return getTable().toUpperCase().equals(metadata.getTable().toUpperCase());
    }

    /**
     * 表定义
     *
     * @return 表定义
     */
    String tableDefinition();

    /**
     * 主键
     *
     * @return 主键
     */
    String getPrimaryId();

    /**
     * sql
     *
     * @return insert sql
     */
    default String getInsertSql(String[] column) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO `").append(getTable()).append("`(");
        for (String name : column) {
            stringBuilder.append(name).append(" ,");
        }
        int length = stringBuilder.length();
        stringBuilder.delete(length - 1, length);
        stringBuilder.append(") VALUES(");
        for (int i = 0; i < column.length; i++) {
            stringBuilder.append("? ,");
        }

        length = stringBuilder.length();
        stringBuilder.delete(length - 1, length);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
    /**
     * sql
     *
     * @return insert sql
     */
    default String getInsertSql(BiConsumer<Integer, Column> consumer) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO `").append(getTable()).append("`(");
        for (Column column1 : getColumn()) {
            if (column1.isPrimary()) {
                continue;
            }
            stringBuilder.append(column1.getName()).append(" ,");
        }
        int length = stringBuilder.length();
        stringBuilder.delete(length - 1, length);
        stringBuilder.append(") VALUES(");
        List<Column> column = getColumn();
        int index = 0;
        for (int i = 0; i < column.size(); i++) {
            Column column1 = column.get(i);
            if (column1.isPrimary()) {
                continue;
            }

            consumer.accept(index++, column1);
            stringBuilder.append("? ,");
        }

        length = stringBuilder.length();
        stringBuilder.delete(length - 1, length);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    /**
     * 非主键数量
     *
     * @return 非主键数量
     */
    default int getColumnSize() {
        return (int) getColumn().stream().filter(it -> !it.isPrimary()).count();
    }

    /**
     * 查询语句
     * @param columnName 查询字段
     * @return 查询语句
     */
    default String getQuerySql(String... columnName) {
        return "SELECT  " + Joiner.on(',').join(columnName) + " FROM `" + getTable() + "`";
    }

    /**
     * 查询语句
     * @param columnName 查询字段
     * @return 查询语句
     */
    default String getQuerySqlOrderByPrimary(String... columnName) {
        String querySql = getQuerySql(columnName);
        String primaryId = getPrimaryId();
        return StringUtils.isNullOrEmpty(primaryId) ? querySql : querySql + " ORDER BY " + primaryId + " ASC";
    }

    /**
     * 索引
     * @return 索引
     */
    List<Index> getIndex();

    /**
     * 是否有主键
     * @return 是否有主键
     */
    default boolean havePK() {
        List<Column> column = getColumn();
        for (Column column1 : column) {
            if(column1.isPrimary()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取主键
     * @return 主键
     */
    default String getKeyProperty() {
        List<Column> column = getColumn();
        for (Column column1 : column) {
            if(column1.isPrimary()) {
                return column1.getName();
            }
        }
        return null;
    }

    /**
     * 获取主键
     * @return 主键
     */
    default Column getKeyColumn() {
        List<Column> column = getColumn();
        for (Column column1 : column) {
            if(column1.isPrimary()) {
                return column1;
            }
        }
        return null;
    }

    /**
     * 选择字段
     * @param predicate 回调
     * @return 结果
     */
    default String chooseSelect(Predicate<Column> predicate) {
        String sqlSelect = getKeyProperty();
        String fieldsSqlSelect = getColumn().stream().filter(predicate)
                .map(Column::getName).collect(joining(SYMBOL_COMMA));
        if (StringUtils.isNotBlank(sqlSelect) && StringUtils.isNotBlank(fieldsSqlSelect)) {
            return sqlSelect + SYMBOL_COMMA + fieldsSqlSelect;
        } else if (StringUtils.isNotBlank(fieldsSqlSelect)) {
            return fieldsSqlSelect;
        }
        return sqlSelect;
    }


    /**
     * 获取对象属性值
     *
     * @param entity   对象
     * @param property 属性名
     * @return 属性值
     * @since 3.4.4
     */
    default Object getPropertyValue(Object entity, String property) {
        try {
            return ClassUtils.getFieldValue(property, entity);
        } catch (Exception e) {
            throw new RuntimeException(StringUtils.format("Error: Cannot read property in {}.  Cause:", e, entity.getClass().getSimpleName()));
        }
    }
}
