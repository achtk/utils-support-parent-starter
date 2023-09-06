package com.chua.common.support.database.orm.conditions;

import com.chua.common.support.database.orm.conditions.query.LambdaSqlQueryWrapper;
import com.chua.common.support.database.orm.conditions.query.SqlQueryWrapper;
import com.chua.common.support.database.orm.conditions.segments.MergeSegments;
import com.chua.common.support.database.orm.conditions.update.LambdaSqlUpdateWrapper;
import com.chua.common.support.database.orm.conditions.update.SqlUpdateWrapper;

import java.util.Collections;
import java.util.Map;

/**
 * 条件构造
 *
 * @author Caratacus
 */
public final class SqlWrappers {

    /**
     * 空的 EmptyWrapper
     */
    private static final SqlQueryWrapper<?> QUERY_WRAPPER = new EmptyWrapper<>();

    private SqlWrappers() {
        // ignore
    }

    /**
     * 获取 this
     *
     * @param <T> 实体类泛型
     * @return this
     */
    public static <T> SqlQueryWrapper<T> query() {
        return new SqlQueryWrapper<>();
    }

    /**
     * 获取 this
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return this
     */
    public static <T> SqlQueryWrapper<T> query(T entity) {
        return new SqlQueryWrapper<>(entity);
    }

    /**
     * 获取 this
     *
     * @param <T> 实体类泛型
     * @return this
     */
    public static <T> LambdaSqlQueryWrapper<T> lambdaQuery() {
        return new LambdaSqlQueryWrapper<>();
    }

    /**
     * 获取 this
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return this
     */
    public static <T> LambdaSqlQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaSqlQueryWrapper<>(entity);
    }

    /**
     * 获取 this
     *
     * @param entityClass 实体类class
     * @param <T>         实体类泛型
     * @return this
     * @since 3.3.1
     */
    public static <T> LambdaSqlQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        return new LambdaSqlQueryWrapper<>(entityClass);
    }

    /**
     * 获取 this
     *
     * @param <T> 实体类泛型
     * @return this
     */
    public static <T> SqlUpdateWrapper<T> update() {
        return new SqlUpdateWrapper<>();
    }

    /**
     * 获取 this
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return this
     */
    public static <T> SqlUpdateWrapper<T> update(T entity) {
        return new SqlUpdateWrapper<>(entity);
    }

    /**
     *
     * @param <T> 实体类泛型
     * @return w
     */
    public static <T> LambdaSqlUpdateWrapper<T> lambdaUpdate() {
        return new LambdaSqlUpdateWrapper<>();
    }

    /**
     * 获取
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @returnw
     */
    public static <T> LambdaSqlUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaSqlUpdateWrapper<>(entity);
    }

    /**
     * 获取
     *
     * @param entityClass 实体类class
     * @param <T>         实体类泛型
     * @return this
     * @since 3.3.1
     */
    public static <T> LambdaSqlUpdateWrapper<T> lambdaUpdate(Class<T> entityClass) {
        return new LambdaSqlUpdateWrapper<>(entityClass);
    }

    /**
     * 获取 EmptyWrapper&lt;T&gt;
     *
     * @param <T> 任意泛型
     * @return EmptyWrapper&lt;T&gt;
     * @see EmptyWrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> SqlQueryWrapper<T> emptyWrapper() {
        return (SqlQueryWrapper<T>) QUERY_WRAPPER;
    }

    /**
     * 一个空的QueryWrapper子类该类不包含任何条件
     *
     * @param <T>
     * @see SqlQueryWrapper
     */
    private static class EmptyWrapper<T> extends SqlQueryWrapper<T> {

        private static final long serialVersionUID = -2515957613998092272L;

        @Override
        public T getEntity() {
            return null;
        }

        @Override
        public EmptyWrapper<T> setEntity(T entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SqlQueryWrapper<T> setEntityClass(Class<T> entityClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<T> getEntityClass() {
            return null;
        }

        @Override
        public String getSqlSelect() {
            return null;
        }

        @Override
        public MergeSegments getExpression() {
            return null;
        }

        @Override
        public boolean isEmptyOfWhere() {
            return true;
        }

        @Override
        public boolean isEmptyOfNormal() {
            return true;
        }

        @Override
        public boolean nonEmptyOfEntity() {
            return !isEmptyOfEntity();
        }

        @Override
        public boolean isEmptyOfEntity() {
            return true;
        }

        @Override
        protected void initNeed() {
        }

        @Override
        public EmptyWrapper<T> last(boolean condition, String lastSql) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSqlSegment() {
            return null;
        }

        @Override
        public Map<String, Object> getParamNameValuePairs() {
            return Collections.emptyMap();
        }

        @Override
        protected String columnsToString(String... columns) {
            return null;
        }

        @Override
        protected String columnToString(String column) {
            return null;
        }

        @Override
        protected EmptyWrapper<T> instance() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
