package com.chua.common.support.database.orm.enums;


import com.chua.common.support.database.orm.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * SQL 保留关键字枚举
 *
 * @author hubin
 * @since 2018-05-28
 */
@AllArgsConstructor
public enum SqlKeyword implements ISqlSegment {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ(SYMBOL_EQUALS),
    NE("<>"),
    GT(SYMBOL_RIGHT_CHEV),
    GE(">="),
    LT(SYMBOL_LEFT_CHEV),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
