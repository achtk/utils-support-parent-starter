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
    /**
     * sql
     */
    AND("AND"),
    /**
     * sql
     */
    OR("OR")
    /**sql*/
    ,
    NOT("NOT"),
    /**
     * sql
     */
    IN("IN"),
    /**
     * sql
     */
    NOT_IN("NOT IN"),
    /**
     * sql
     */
    LIKE("LIKE"),
    /**
     * sql
     */
    NOT_LIKE("NOT LIKE"),
    /**
     * sql
     */
    EQ(SYMBOL_EQUALS),
    /**
     * sql
     */
    NE("<>"),
    /**
     * sql
     */
    GT(SYMBOL_RIGHT_CHEV),
    /**
     * sql
     */
    GE(">="),
    /**
     * sql
     */
    LT(SYMBOL_LEFT_CHEV),
    /**
     * sql
     */
    LE("<="),
    /**
     * sql
     */
    IS_NULL("IS NULL"),
    /**
     * sql
     */
    IS_NOT_NULL("IS NOT NULL"),
    /**
     * sql
     */
    GROUP_BY("GROUP BY"),
    /**
     * sql
     */
    HAVING("HAVING"),
    /**
     * sql
     */
    ORDER_BY("ORDER BY"),
    /**
     * sql
     */
    EXISTS("EXISTS"),
    /**
     * sql
     */
    NOT_EXISTS("NOT EXISTS"),
    /**
     * sql
     */
    BETWEEN("BETWEEN"),
    /**
     * sql
     */
    NOT_BETWEEN("NOT BETWEEN"),
    /**
     * sql
     */
    ASC("ASC"),
    /**
     * sql
     */
    DESC("DESC");

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
