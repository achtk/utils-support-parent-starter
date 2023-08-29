
package com.chua.common.support.database.orm.conditions.segments;

import com.chua.common.support.database.orm.conditions.ISqlSegment;

import java.util.ArrayList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.database.orm.enums.SqlKeyword.ORDER_BY;
import static java.util.stream.Collectors.joining;

/**
 * Order By SQL 片段
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public class OrderBySegmentList extends AbstractSegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        final List<ISqlSegment> sqlSegmentList = new ArrayList<>(list);
        list.clear();
        list.add(() -> sqlSegmentList.stream().map(ISqlSegment::getSqlSegment).collect(joining(SYMBOL_SPACE)));
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(SYMBOL_COMMA, SYMBOL_SPACE + ORDER_BY.getSqlSegment() + SYMBOL_SPACE, SYMBOL_EMPTY));
    }
}
