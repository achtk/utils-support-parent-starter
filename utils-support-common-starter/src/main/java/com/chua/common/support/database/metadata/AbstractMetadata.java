package com.chua.common.support.database.metadata;

import com.chua.common.support.context.resolver.NamePair;
import com.chua.common.support.context.resolver.NamedResolver;
import com.chua.common.support.context.resolver.factory.SimpleNamedResolver;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import com.chua.common.support.file.export.ExportProperty;
import com.chua.common.support.function.strategy.name.CamelUnderscoreNamedStrategy;
import com.chua.common.support.function.strategy.name.NamedStrategy;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 元数据信息
 *
 * @author CH
 */
public abstract class AbstractMetadata<T> implements Metadata<T> {

    protected String table;
    /**
     * 命名策略
     */
    private NamedStrategy columnNamedStrategy;
    /**
     * 映射解析器
     */
    private NamedResolver namedResolver;
    /**
     * 命名策略
     */
    private NamedStrategy tableNamedStrategy;
    private Class<T> type;

    private String tableName;
    private String databaseName;
    private String definition;

    @Getter
    private Map<String, String> mapping = new LinkedHashMap<>();
    protected Class<ExportProperty> mappingAnnotationType = ExportProperty.class;


    private final List<Column> columnMap = new LinkedList<>();
    /**
     * 是否全部字段作为表字段
     */
    protected boolean isAll;
    /**
     * 索引
     */
    private List<Index> indexs;

    private String prefix = "";

    private String suffix = "";
    private Column primary;
    private String tableComment;
    protected String catalog;

    public AbstractMetadata(Class<T> type, String prefix, String suffix) {
        this(new CamelUnderscoreNamedStrategy(), new CamelUnderscoreNamedStrategy(), new SimpleNamedResolver(), type, true, prefix, suffix);
    }

    @Override
    public Class<?> getJavaType() {
        return type;
    }

    public AbstractMetadata(Class<T> type) {
        this(new CamelUnderscoreNamedStrategy(), new CamelUnderscoreNamedStrategy(), new SimpleNamedResolver(), type, true, null, null);
    }

    public AbstractMetadata(NamedStrategy columnNamedStrategy,
                            NamedStrategy tableNamedStrategy,
                            NamedResolver namedResolver,
                            Class<T> type,
                            boolean isAll, String prefix, String suffix
                            ) {
        this.columnNamedStrategy = columnNamedStrategy;
        this.tableNamedStrategy = tableNamedStrategy;
        this.namedResolver = namedResolver;
        this.type = type;
        this.isAll = isAll;
        this.prefix = StringUtils.defaultString(prefix, "");
        this.suffix = StringUtils.defaultString(suffix, "");
        this.analysis();
    }

    @Override
    public Metadata<T> setTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @Override
    public Metadata<T> setDatabase(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    @Override
    public Metadata<T> addColumn(Column column) {
        this.columnMap.add(column);
        return this;
    }

    @Override
    public String getTable() {
        return prefix + tableName + suffix;
    }

    @Override
    public String getTableComment() {
        return tableComment;
    }

    @Override
    public String getDatabase() {
        return databaseName;
    }

    @Override
    public List<Column> getColumn() {
        return columnMap;
    }

    @Override
    public String tableDefinition() {
        return StringUtils.defaultString(definition, "");
    }

    @Override
    public String getPrimaryId() {
        return null == primary ? null : primary.getName();
    }

    /**
     * 解析对象
     */
    private void analysis() {
        this.tableName = analysisTable(type, tableNamedStrategy);
        this.tableComment = analysisTableComment(type);
        this.catalog = analysisTableCatalog(type);
        this.databaseName = analysisDatabase(type);
        this.indexs = analysisIndex(type);
        this.definition = analysisDefinition(type);
        ClassUtils.doWithFields(type, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }

            if (field.getName().contains("this$")) {
                return;
            }

            Column column = analysisColumn(field, columnNamedStrategy);
            if (null == column) {
                return;
            }

            String[] name = namedResolver.resolve(NamePair.builder().annotationType(ExportProperty.class).type(field).build());
            for (String s : name) {
                mapping.put(s, column.getName());
            }


            column.setJavaType(field.getType());
            if(StringUtils.isNullOrEmpty(column.getName())) {
                column.setName(columnNamedStrategy.named(field.getName()));
            }

            columnMap.add(column);
        });

        for (Column column : columnMap) {
            if(column.isPrimary()) {
                this.primary = column;
                break;
            }
        }
    }

    /**
     * 定义
     * @param type 类型
     * @return 定义
     */
    abstract String analysisDefinition(Class<T> type);
    /**
     * 分析索引
     * @param type 类型
     * @return 索引
     */
    abstract List<Index> analysisIndex(Class<T> type);

    /**
     * 数据库
     *
     * @param type 类型
     * @return 数据库
     */
    abstract String analysisDatabase(Class<?> type);

    /**
     * 解析表描述
     *
     * @param type 类型
     * @return 表名称
     */
    abstract String analysisTableComment(Class<?> type);

    /**
     * catalog
     *
     * @param type 类型
     * @return 表名称
     */
    abstract String analysisTableCatalog(Class<?> type);

    /**
     * 解析表
     *
     * @param type               类型
     * @param tableNamedStrategy 策略
     * @return 表名称
     */
    abstract String analysisTable(Class<?> type, NamedStrategy tableNamedStrategy);

    /**
     * 字段
     * @param field 字段
     * @param columnNamedStrategy 策略
     * @return 字段
     */
    abstract Column analysisColumn(Field field, NamedStrategy columnNamedStrategy);
}
