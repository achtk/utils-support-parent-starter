
package com.chua.common.support.database.orm.conditions.query;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.AbstractSqlWrapper;
import com.chua.common.support.database.orm.conditions.SharedString;
import com.chua.common.support.database.orm.conditions.segments.MergeSegments;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * Entity 对象封装操作类
 *
 * @author hubin miemie HCL
 * @since 2018-05-25
 */
@SuppressWarnings("serial")
public class SqlQueryWrapper<T> extends AbstractSqlWrapper<T, String, SqlQueryWrapper<T>>
    implements Query<SqlQueryWrapper<T>, T, String> {

    /**
     * 查询字段
     */
    private final SharedString sqlSelect = new SharedString();

    public SqlQueryWrapper() {
        this(null);
    }

    public SqlQueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public SqlQueryWrapper(T entity, String... columns) {
        super.setEntity(entity);
        super.initNeed();
        this.select(columns);
    }

    /**
     * 非对外公开的构造方法,只用于生产嵌套 sql
     *
     * @param entityClass 本不应该需要的
     */
    private SqlQueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                            Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                            SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    public SqlQueryWrapper<T> select(boolean condition, List<String> columns) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(String.join(SYMBOL_COMMA, columns));
        }
        return typedThis;
    }

    @Override
    public SqlQueryWrapper<T> select(Class<T> entityClass, Predicate<Column> predicate) {
        super.setEntityClass(entityClass);
        this.sqlSelect.setStringValue(Metadata.of(getEntityClass()).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    @Override
    protected String columnSqlInjectFilter(String column) {
        return StringUtils.sqlInjectionReplaceBlank(column);
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public LambdaSqlQueryWrapper<T> lambda() {
        return new LambdaSqlQueryWrapper<>(getEntity(), getEntityClass(), sqlSelect, paramNameSeq, paramNameValuePairs,
            expression, paramAlias, lastSql, sqlComment, sqlFirst);
    }

    /**
     * 用于生成嵌套 sql
     * <p>
     * 故 sqlSelect 不向下传递
     * </p>
     */
    @Override
    protected SqlQueryWrapper<T> instance() {
        return new SqlQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs, new MergeSegments(),
            paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
    }
}
