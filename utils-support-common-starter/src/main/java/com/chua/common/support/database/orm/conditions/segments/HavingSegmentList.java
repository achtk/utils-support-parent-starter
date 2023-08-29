
package com.chua.common.support.database.orm.conditions.segments;

import com.chua.common.support.database.orm.conditions.ISqlSegment;
import com.chua.common.support.database.orm.enums.SqlKeyword;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_EMPTY;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_SPACE;
import static com.chua.common.support.database.orm.enums.SqlKeyword.HAVING;
import static java.util.stream.Collectors.joining;

/**
 * Having SQL 片段
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public class HavingSegmentList extends AbstractSegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (!isEmpty()) {
            this.add(SqlKeyword.AND);
        }
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return SYMBOL_EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(SYMBOL_SPACE,
                SYMBOL_SPACE + HAVING.getSqlSegment() + SYMBOL_SPACE,
                SYMBOL_EMPTY));
    }
}
