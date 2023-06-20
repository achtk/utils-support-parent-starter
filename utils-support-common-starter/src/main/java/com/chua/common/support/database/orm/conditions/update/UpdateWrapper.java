
package com.chua.common.support.database.orm.conditions.update;

import com.chua.common.support.database.orm.conditions.AbstractWrapper;
import com.chua.common.support.database.orm.conditions.SharedString;
import com.chua.common.support.database.orm.conditions.segments.MergeSegments;
import com.chua.common.support.database.orm.conditions.update.LambdaUpdateWrapper;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_EQUALS;

/**
 * Update 条件封装
 *
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
@SuppressWarnings("serial")
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>>
    implements Update<UpdateWrapper<T>, String> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    public UpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this(null);
    }

    public UpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    private UpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                          SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(SYMBOL_COMMA, sqlSet);
    }

    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object val, String mapping) {
        return maybeDo(condition, () -> {
            String sql = formatParam(mapping, val);
            sqlSet.add(column + SYMBOL_EQUALS + sql);
        });
    }

    @Override
    public UpdateWrapper<T> setSql(boolean condition, String sql) {
        if (condition && StringUtils.isNotBlank(sql)) {
            sqlSet.add(sql);
        }
        return typedThis;
    }

    @Override
    protected String columnSqlInjectFilter(String column) {
        return StringUtils.sqlInjectionReplaceBlank(column);
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public LambdaUpdateWrapper<T> lambda() {
        return new LambdaUpdateWrapper<>(getEntity(), getEntityClass(), sqlSet, paramNameSeq, paramNameValuePairs,
            expression, paramAlias, lastSql, sqlComment, sqlFirst);
    }

    @Override
    protected UpdateWrapper<T> instance() {
        return new UpdateWrapper<>(getEntity(), null, paramNameSeq, paramNameValuePairs, new MergeSegments(),
            paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSet.clear();
    }
}
