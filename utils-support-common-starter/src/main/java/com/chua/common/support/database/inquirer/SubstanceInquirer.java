package com.chua.common.support.database.inquirer;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 实体查询器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class SubstanceInquirer<T> implements SqlSubstanceInquirer<T> {
    private final Class<T> type;
    private final boolean convertAllToFields;
    private final String table;
    private final JdbcInquirer jdbcInquirer;
    private String primaryKey;
    private final List<String> columns = new LinkedList<>();
    public static final String JAVAX_TABLE = "javax.persistence.Table";
    private static final String MYBATIS_TABLE = "com.baomidou.mybatisplus.annotation.TableName";
    private static final String JAVAX_COLUMN = "javax.persistence.Column";
    private static final String MYBATIS_COLUMN = "com.baomidou.mybatisplus.annotation.TableField";
    private static final String JAVAX_ID = "javax.persistence.Id";
    private static final String MYBATIS_ID = "com.baomidou.mybatisplus.annotation.TableId";

    private static final Class<Annotation> MYBATIS_TABLE_TYPE;
    private static final Class<Annotation> JAVAX_TABLE_TYPE;
    private static final Class<Annotation> JAVAX_COLUMN_TYPE;
    private static final Class<Annotation> MYBATIS_COLUMN_TYPE;
    private static final Class<Annotation> JAVAX_ID_TYPE;
    private static final Class<Annotation> MYBATIS_ID_TYPE;

    static {
        JAVAX_TABLE_TYPE = (Class<Annotation>) ClassUtils.forName(JAVAX_TABLE);
        MYBATIS_TABLE_TYPE = (Class<Annotation>) ClassUtils.forName(MYBATIS_TABLE);
        JAVAX_COLUMN_TYPE = (Class<Annotation>) ClassUtils.forName(JAVAX_COLUMN);
        MYBATIS_COLUMN_TYPE = (Class<Annotation>) ClassUtils.forName(MYBATIS_COLUMN);
        JAVAX_ID_TYPE = (Class<Annotation>) ClassUtils.forName(JAVAX_ID);
        MYBATIS_ID_TYPE = (Class<Annotation>) ClassUtils.forName(MYBATIS_ID);
    }

    public SubstanceInquirer(DataSource dataSource, boolean isAutoCommit, Class<T> type, boolean convertAllToFields) {
        this.type = type;
        this.jdbcInquirer = new JdbcInquirer(dataSource, isAutoCommit);
        this.convertAllToFields = convertAllToFields;
        this.table = analysis();
    }

    /**
     * 分析表
     *
     * @return 表
     */
    private String analysis() {
        String table = analysisTable();
        analysisColumns();
        return table;
    }

    /**
     * 分析字段
     */
    private void analysisColumns() {
        ClassUtils.doWithFields(type, new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                analysisPrimaryKey(field);
                analysisColumns(field);
            }

        });
    }

    /**
     * 分析字段
     *
     * @param field 字段
     */
    private void analysisColumns(Field field) {
        if (null != MYBATIS_ID_TYPE) {
            Map<String, Object> map = AnnotationUtils.asMap(field.getDeclaredAnnotation(MYBATIS_COLUMN_TYPE));
            if (!map.isEmpty()) {
                String value = MapUtils.getString(map, "value");
                if (!StringUtils.isNullOrEmpty(value)) {
                    columns.add(value);
                    return;
                }
                columns.add(NamingCase.toCamelUnderscore(field.getName()));
            }
            return;
        }

        if (null != MYBATIS_ID_TYPE) {
            Map<String, Object> map = AnnotationUtils.asMap(field.getDeclaredAnnotation(JAVAX_COLUMN_TYPE));
            if (!map.isEmpty()) {
                String value = MapUtils.getString(map, "name");
                if (!StringUtils.isNullOrEmpty(value)) {
                    columns.add(value);
                    return;
                }
                columns.add(NamingCase.toCamelUnderscore(field.getName()));
            }
            return;
        }

        if (convertAllToFields) {
            columns.add(NamingCase.toCamelUnderscore(field.getName()));
        }
    }

    /**
     * 分析主键
     *
     * @param field 字段
     */
    private void analysisPrimaryKey(Field field) {
        if (null != MYBATIS_ID_TYPE) {
            Map<String, Object> map = AnnotationUtils.asMap(field.getDeclaredAnnotation(MYBATIS_ID_TYPE));
            if (!map.isEmpty()) {
                String value = MapUtils.getString(map, "value");
                if (!StringUtils.isNullOrEmpty(value)) {
                    this.primaryKey = value;
                    return;
                }
                this.primaryKey = NamingCase.toCamelUnderscore(field.getName());
            }
        }

        if (null != JAVAX_ID_TYPE) {
            Annotation annotation = field.getDeclaredAnnotation(JAVAX_ID_TYPE);
            if (null != annotation) {
                this.primaryKey = NamingCase.toCamelUnderscore(field.getName());
            }
        }
    }

    /**
     * 分析表
     *
     * @return 表
     */
    private String analysisTable() {
        String table = null;
        if (null != MYBATIS_TABLE_TYPE) {
            Map<String, Object> map = AnnotationUtils.asMap(type.getDeclaredAnnotation(MYBATIS_TABLE_TYPE));
            table = MapUtils.getString(map, "value");
            if (!StringUtils.isNullOrEmpty(table)) {
                return table;
            }
        }

        if (null != JAVAX_TABLE_TYPE) {
            Map<String, Object> map = AnnotationUtils.asMap(type.getDeclaredAnnotation(JAVAX_TABLE_TYPE));
            table = MapUtils.getString(map, "name");
            if (!StringUtils.isNullOrEmpty(table)) {
                return table;
            }
        }

        return NamingCase.toCamelUnderscore(type.getSimpleName());
    }

    @Override
    public T updateById(T entity) {
        BeanMap beanMap = BeanMap.of(entity);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(table);

        AtomicReference<Object> key = new AtomicReference<>();
        List<String> columns = new LinkedList<>();
        List<Object> values = new LinkedList<>();
        beanMap.forEach((k, v) -> {
            if (v == null) {
                return;
            }

            if (v instanceof String && v.toString().length() == 0) {
                return;
            }

            if (primaryKey.equalsIgnoreCase(k.toString())) {
                key.set(v);
                return;
            }
            columns.add(NamingCase.toCamelUnderscore(k.toString()) + "= ?");
            values.add(v);
        });

        sb.append("SET ");
        sb.append(Joiner.on(',').join(columns));
        sb.append(" WHERE ").append(primaryKey).append("= ?");
        values.add(key.get());

        jdbcInquirer.update(sb.toString(), values.toArray(new Object[0]));

        return entity;
    }

    @Override
    public T save(T entity) {
        BeanMap beanMap = BeanMap.of(entity);
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table);
        Map<String, Object> item = new HashMap<>();

        sb.append("(");
        List<String> columns = new LinkedList<>();
        List<Object> values = new LinkedList<>();
        beanMap.forEach((k, v) -> {
            if (v == null) {
                return;
            }

            if (v instanceof String && v.toString().length() == 0) {
                return;
            }

            item.put(k.toString(), v);
            columns.add(NamingCase.toCamelUnderscore(k.toString()));
            values.add(v);
        });


        sb.append(Joiner.on(',').join(columns)).append(")");
        sb.append(" VALUES (").append(Joiner.on(',').join(StringUtils.repeat("?", columns.size()).split(""))).append(")");

        Map insert = jdbcInquirer.insert(sb.toString(), values.toArray(new Object[0]), Map.class);
        item.put(primaryKey, MapUtils.getString(insert, "GENERATED_KEY"));
        BeanUtils.copyProperties(item, entity);
        return entity;
    }

    @Override
    public T queryById(Serializable id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(table);
        sb.append(" WHERE 1 = 1 ").append(" AND ").append(primaryKey).append(" = ?");
        return jdbcInquirer.queryOne(sb.toString(), new Object[]{id}, type);
    }

    @Override
    public List<T> query(T entity) {
        BeanMap beanMap = BeanMap.of(entity);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(table);

        List<String> columns = new LinkedList<>();
        List<Object> values = new LinkedList<>();
        beanMap.forEach((k, v) -> {
            if (v == null) {
                return;
            }

            columns.add(NamingCase.toCamelUnderscore(k.toString()));
            values.add(v);
        });


        if (!columns.isEmpty()) {
            sb.append(" WHERE 1 = 1 ");
            for (int i = 0, columnsSize = columns.size(); i < columnsSize; i++) {
                String column = columns.get(i);
                sb.append(" AND ").append(column).append(" = ?");
            }
        }
        return jdbcInquirer.query(sb.toString(), values.toArray(new Object[0]), type);
    }

    @Override
    public DataSource getDataSource() {
        return jdbcInquirer.dataSource;
    }

    @Override
    public int execute(String command, Object... args) throws Exception {
        return 0;
    }

    @Override
    public List<Column> getColumn(String tableName) {
        return jdbcInquirer.getColumn(tableName);
    }

    public void close() throws Exception {
        jdbcInquirer.close();
    }
}
