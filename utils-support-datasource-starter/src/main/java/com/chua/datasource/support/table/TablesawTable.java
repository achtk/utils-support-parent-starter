package com.chua.datasource.support.table;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.ClassUtils;
import com.chua.datasource.support.rule.AbstractRuleTable;
import com.chua.datasource.support.rule.TableEnumerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.sql.type.SqlTypeName;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.table.ConnectorMetadata.DIRECTORY;

/**
 * tablesaw
 *
 * @author CH
 */
@Slf4j
@SuppressWarnings("ALL")
public class TablesawTable extends AbstractRuleTable {
    private final Table table;

    /**
     * Creates a RuleTable.
     *
     * @param profile 配置
     * @param types               字段与类型
     */
    public TablesawTable(Profile profile, Map<String, SqlTypeName> types) {
        super(profile, types);
        this.table = createRealTable(types);
        if (log.isDebugEnabled()) {
            log.debug(table.print());
        }
    }

    /**
     * 创建表
     *
     * @param types 字段
     * @return 表
     */
    private Table createRealTable(Map<String, SqlTypeName> types) {
        Table table1 = Table.create(profile.getString("name"));
        List<Column> columns = new LinkedList<>();
        for (Map.Entry<String, SqlTypeName> entry : types.entrySet()) {
            Column column = createColumn(entry);
            columns.add(column);
            table1.addColumns(column);
        }

        doAnalysisData(table1.columns());
        return table1;
    }

    /**
     * 渲染数据
     *
     * @param column 字段
     */
    private void doAnalysisData(List<Column<?>> column) {
        List data = profile.getType("data", Collections.emptyList(), List.class);
        if (data.isEmpty()) {
            return;
        }

        for (Object datum : data) {
            if (datum instanceof Map) {
                doAnalysisColumnData(column, (Map) datum);
            }
        }
    }

    /**
     * 渲染数据
     *
     * @param column 字段
     * @param datum  数据
     */
    private void doAnalysisColumnData(List<Column<?>> column, Map datum) {
        for (Column column1 : column) {
            Object o = datum.get(column1.name());
            column1.append(Converter.convertIfNecessary(o, ClassUtils.getActualTypeArguments(column1.getClass().getSuperclass())[1]));
        }
    }

    /**
     * 渲染字段
     *
     * @param entry 字段-类型
     * @return 字段
     */
    private Column createColumn(Map.Entry<String, SqlTypeName> entry) {
        SqlTypeName sqlTypeName = entry.getValue();
        if (sqlTypeName == SqlTypeName.INTEGER) {
            return IntColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.BIGINT) {
            return LongColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.TINYINT) {
            return ShortColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.TIME) {
            return TimeColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.FLOAT) {
            return FloatColumn.create(entry.getKey());
        }


        if (sqlTypeName == SqlTypeName.DOUBLE) {
            return DoubleColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.DATE) {
            return DateColumn.create(entry.getKey());
        }

        if (sqlTypeName == SqlTypeName.TIME_WITH_LOCAL_TIME_ZONE) {
            return DateTimeColumn.create(entry.getKey());
        }

        return StringColumn.create(entry.getKey());
    }

    @Override
    protected Enumerable<Object> find(Profile attributes, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields) {
        return Linq4j.emptyEnumerable();
    }

    @Override
    protected Enumerable aggregate(Profile configureAttributes, List<Map.Entry<String, Class>> fields, List<String> operations) {
        Table table1 = table.copy();
        if (hasDirectory()) {
            table1 = doAnalysisDirectoryData(table1);
        }

        for (String operation : operations) {
            JsonObject jsonObject = Converter.convertIfNecessary(operation, JsonObject.class);
            table1 = filter(table1, jsonObject);
        }

        int rowCount = table1.rowCount();
        List rs = new LinkedList<>();
        for (int i = 0; i < rowCount; i++) {
            rs.add(createRow(i, table1, fields));
        }
        return new AbstractEnumerable<Object>() {
            @Override
            public Enumerator<Object> enumerator() {
                return new TableEnumerator(rs);
            }
        };
    }

    /**
     * 数据
     *
     * @param index  索引
     * @param table1 表
     * @param fields 字段
     * @return 结果
     */
    private Object[] createRow(int index, Table table1, List<Map.Entry<String, Class>> fields) {
        Object[] item = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Map.Entry<String, Class> entry = fields.get(i);
            item[i] = table1.column(entry.getKey()).get(index);
        }

        return item;
    }

    /**
     * 过滤
     *
     * @param table1     表
     * @param jsonObject 条件
     * @return 结果
     */
    private Table filter(Table table1, JsonObject jsonObject) {
        Integer limit = jsonObject.getInteger("$limit");
        if (null != limit) {
            return table1.first(limit);
        }

        String sort = jsonObject.getString("$sort");
        if (null != sort) {

        }


        Integer skip = jsonObject.getInteger("$skip");
        if (null != skip) {
            for (int i = 0; i < skip; i++) {
                table1 = table1.dropRows(i);
            }

            return table1;
        }

        String project = jsonObject.getString("$project");
        if (null != project) {

        }

        JsonObject match = jsonObject.getType("$match", new JsonObject(), JsonObject.class);
        if (null != match) {
            for (Map.Entry<String, Object> entry : match.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!(value instanceof Map)) {
                    return eq(table1, key, value);
                }
                return map(table1, key, (Map) value);
            }
        }
        return table1;
    }

    private Table map(Table table1, String column, Map<String, Object> value) {
        Table table2 = table1;
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            Table finalTable = table2;
            table2 = table2.where(QuerySupport.all(it -> {
                return analysisSelection(finalTable, column, entry.getKey(), entry.getValue());
            }));
        }

        return table2;
    }

    private Selection analysisSelection(Table finalTable, String column, String key, Object value) {
        if ("$gt".equals(key)) {
            return analysisSelectionGtColumn(finalTable.column(column), value);
        }

        if ("$gte".equals(key)) {
            return analysisSelectionGteColumn(finalTable.column(column), value);
        }

        if ("$lt".equals(key)) {
            return analysisSelectionLtColumn(finalTable.column(column), value);
        }

        if ("$gle".equals(key)) {
            return analysisSelectionLteColumn(finalTable.column(column), value);
        }
        return analysisSelectionColumn(finalTable.column(column), value);
    }

    private Selection analysisSelectionColumn(Column<?> column, Object value) {
        if (column instanceof StringColumn) {
            return ((StringColumn) column).isEqualTo(Converter.convertIfNecessary(value, String.class));
        }

        if (column instanceof NumericColumn) {
            return ((NumericColumn) column).isEqualTo(Converter.convertIfNecessary(value, double.class));
        }
        return column.isMissing();
    }

    private Selection analysisSelectionLteColumn(Column<?> column, Object value) {
        if (column instanceof NumericColumn) {
            return ((NumericColumn) column).isLessThanOrEqualTo(Converter.convertIfNecessary(value, double.class));
        }


        if (column instanceof StringColumn) {
            return ((StringColumn) column).isEqualTo(Converter.convertIfNecessary(value, String.class));
        }

        return column.isMissing();
    }

    private Selection analysisSelectionLtColumn(Column<?> column, Object value) {
        if (column instanceof NumericColumn) {
            return ((NumericColumn) column).isLessThan(Converter.convertIfNecessary(value, double.class));
        }
        return column.isMissing();
    }

    private Selection analysisSelectionGteColumn(Column<?> column, Object value) {
        if (column instanceof NumericColumn) {
            return ((NumericColumn) column).isGreaterThanOrEqualTo(Converter.convertIfNecessary(value, double.class));
        }

        if (column instanceof StringColumn) {
            return ((StringColumn) column).isEqualTo(Converter.convertIfNecessary(value, String.class));
        }

        return column.isMissing();
    }

    private Selection analysisSelectionGtColumn(Column<?> column, Object value) {
        if (column instanceof NumericColumn) {
            ((NumericColumn) column).isGreaterThan(Converter.convertIfNecessary(value, double.class));
        }

        return column.isMissing();
    }


    private Table eq(Table table1, String key, Object value) {
        return table1.where(QuerySupport.all(it -> {
            Column<?> column = it.column(key);
            return analysisSelectionColumn(column, value);
        }));
    }

    /**
     * 创建数据
     *
     * @param table1 表
     * @return
     */
    private Table doAnalysisDirectoryData(Table table1) {
        String string = profile.getString(DIRECTORY);
        try {
            return table1.read().url(Converter.convertIfNecessary(string, URL.class));
        } catch (Exception e) {
            return table1;
        }
    }

    private boolean hasDirectory() {
        return profile.getObject(DIRECTORY) != null;
    }


}
