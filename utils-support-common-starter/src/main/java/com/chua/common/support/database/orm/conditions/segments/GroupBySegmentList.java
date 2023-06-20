
package com.chua.common.support.database.orm.conditions.segments;

import com.chua.common.support.database.orm.conditions.ISqlSegment;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.database.orm.enums.SqlKeyword.GROUP_BY;
import static java.util.stream.Collectors.joining;

/**
 * Group By SQL 片段
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public class GroupBySegmentList extends AbstractISegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(
                SYMBOL_COMMA,
                SYMBOL_SPACE + GROUP_BY.getSqlSegment() + SYMBOL_SPACE, SYMBOL_EMPTY));
    }
}
