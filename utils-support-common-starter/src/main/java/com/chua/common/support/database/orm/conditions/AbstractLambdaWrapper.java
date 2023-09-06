
package com.chua.common.support.database.orm.conditions;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.orm.PropertyNames;
import com.chua.common.support.lang.lambda.LambdaMeta;
import com.chua.common.support.lang.lambda.LambdaUtils;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static java.util.stream.Collectors.joining;

/**
 * Lambda 语法使用
 * <p>统一处理解析 lambda 获取 column</p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("serial")
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
    extends AbstractSqlWrapper<T, SerFunction<T, ?>, Children> {

    private Map<String, Column> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    @SafeVarargs
    protected final String columnsToString(SerFunction<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    @SafeVarargs
    protected final String columnsToString(boolean onlyColumn, SerFunction<T, ?>... columns) {
        return columnsToString(onlyColumn, Arrays.asList(columns));
    }

    protected final String columnsToString(boolean onlyColumn, List<SerFunction<T, ?>> columns) {
        return columns.stream().map(i -> columnToString(i, onlyColumn)).collect(joining(SYMBOL_COMMA));
    }

    @Override
    protected String columnToString(SerFunction<T, ?> column) {
        return columnToString(column, true);
    }

    protected String columnToString(SerFunction<T, ?> column, boolean onlyColumn) {
        Column cache = getColumnCache(column);
        return cache.getName();
    }

    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @return 列
     */
    protected Column getColumnCache(SerFunction<T, ?> column) {
        LambdaMeta meta = LambdaUtils.extract(column);
        String fieldName = PropertyNames.methodToProperty(meta.getImplMethodName());
        Class<?> instantiatedClass = meta.getInstantiatedClass();
        tryInitCache(instantiatedClass);
        return getColumnCache(fieldName, instantiatedClass);
    }

    private void tryInitCache(Class<?> lambdaClass) {
        if (!initColumnMap) {
            final Class<T> entityClass = getEntityClass();
            if (entityClass != null) {
                lambdaClass = entityClass;
            }
            columnMap = LambdaUtils.getColumnMap(lambdaClass);
            Preconditions.notNull(columnMap, "can not find lambda cache for this entity [%s]", lambdaClass.getName());
            initColumnMap = true;
        }
    }

    private Column getColumnCache(String fieldName, Class<?> lambdaClass) {
        Column columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        if(null == columnCache) {
            columnCache = columnMap.get(LambdaUtils.formatKey(NamingCase.toCamelUnderscore(fieldName)));
        }
        Preconditions.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
            fieldName, lambdaClass.getName());
        return columnCache;
    }
}
