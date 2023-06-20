
package com.chua.common.support.database.orm.conditions;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.segments.MergeSegments;
import com.chua.common.support.database.orm.conditions.segments.NormalSegmentList;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 条件构造抽象类
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("all")
public abstract class Wrapper<T> implements ISqlSegment {

    /**
     * 实体对象（子类实现）
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    public String getSqlSelect() {
        return null;
    }

    public String getSqlSet() {
        return null;
    }

    public String getSqlComment() {
        return null;
    }

    public String getSqlFirst() {
        return null;
    }

    /**
     * 获取 MergeSegments
     */
    public abstract MergeSegments getExpression();

    /**
     * 获取自定义SQL 简化自定义XML复杂情况
     * <p>
     * 使用方法: `select xxx from table` + ${ew.customSqlSegment}
     * <p>
     * 注意事项:
     * 1. 逻辑删除需要自己拼接条件 (之前自定义也同样)
     * 2. 不支持wrapper中附带实体的情况 (wrapper自带实体会更麻烦)
     * 3. 用法 ${ew.customSqlSegment} (不需要where标签包裹,切记!)
     * 4. ew是wrapper定义别名,不能使用其他的替换
     */
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return SYMBOL_WHERE + SYMBOL_SPACE + sqlSegment;
                }
            }
        }
        return EMPTY;
    }

    /**
     * 查询条件为空(包含entity)
     */
    public boolean isEmptyOfWhere() {
        return isEmptyOfNormal() && isEmptyOfEntity();
    }

    /**
     * 查询条件不为空(包含entity)
     */
    public boolean nonEmptyOfWhere() {
        return !isEmptyOfWhere();
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean isEmptyOfNormal() {
        return CollectionUtils.isEmpty(getExpression().getNormal());
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean nonEmptyOfNormal() {
        return !isEmptyOfNormal();
    }

    /**
     * 深层实体判断属性
     *
     * @return true 不为空
     */
    public boolean nonEmptyOfEntity() {
        T entity = getEntity();
        if (entity == null) {
            return false;
        }
        Metadata metadata = Metadata.of(entity.getClass());
        if (metadata == null) {
            return false;
        }
        List<Column> column = metadata.getColumn();
        if (column.stream().anyMatch(e -> fieldStrategyMatch(metadata, entity, e))) {
            return true;
        }
        return StringUtils.isNotBlank(metadata.getKeyProperty()) ? Objects.nonNull(metadata.getPropertyValue(entity, metadata.getKeyProperty())) : false;
    }

    /**
     * 根据实体FieldStrategy属性来决定判断逻辑
     */
    private boolean fieldStrategyMatch(Metadata<?> tableInfo, T entity, Column e) {
        switch (e.getWhereStrategy()) {
            case NOT_NULL:
                return Objects.nonNull(tableInfo.getPropertyValue(entity, e.getName()));
            case IGNORED:
                return true;
            case NOT_EMPTY:
                return StringUtils.checkValNotNull(tableInfo.getPropertyValue(entity, e.getName()));
            case NEVER:
                return false;
            default:
                return Objects.nonNull(tableInfo.getPropertyValue(entity, e.getName()));
        }
    }

    /**
     * 深层实体判断属性
     *
     * @return true 为空
     */
    public boolean isEmptyOfEntity() {
        return !nonEmptyOfEntity();
    }

    /**
     * 获取格式化后的执行sql
     *
     * @return sql
     * @since 3.3.1
     */
    public String getTargetSql() {
        return getSqlSegment().replaceAll("#\\{.+?}", "?");
    }

    /**
     * 条件清空
     *
     * @since 3.3.1
     */
    abstract public void clear();
}
